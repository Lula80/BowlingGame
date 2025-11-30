package yuliya.akkuzhyna.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import yuliya.akkuzhyna.dto.FrameDto;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.persistence.Player;

import java.util.*;

import static yuliya.akkuzhyna.utils.Constants.PINS_PER_FRAME;
import static yuliya.akkuzhyna.utils.Constants.ROLLS_PER_FRAME;
import static yuliya.akkuzhyna.utils.Constants.NUM_FRAMES;

@FieldNameConstants
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreKeepingService {

    private final ApplicationEventPublisher eventPublisher;


    /**
     * @param i frames index
     * @param pins list of knocked down pins by roll of length ROLLS_PER_FRAME max
     *             Negative numbers will be classified as FOUL roll and will be reset to 0.
     */
    public void addUpdateFrames(int i, List<Integer> pins, Player player, final long boardId) throws FrameClosedException {

        validateState(i, pins);
        var isBonusFrame = isBonusFrame(i);
        var pinsToDo = (isBonusFrame && !player.getFramesQueue().isEmpty())? player.getFinalRollsToDo() * PINS_PER_FRAME: PINS_PER_FRAME;
        var current = Frame.initFrame( i , pinsToDo, pins);

        current.addPrevClosedScore(
                    getRecentClosedScore(player, isBonusFrame? player.getFinalRollsToDo():current.getRollsMade(),
                            current.getPinsDown(), boardId));
        player.getFramesQueue().offer(current);

        if(!isBonusFrame)
            eventPublisher.publishEvent(new BoardUpdateEvent( mapToDto(current), true, player.getId(), boardId));

    }

    private static FrameDto mapToDto(Frame current) {
        return FrameDto.builder().score(current.getScore()).closed(current.isClosed())
                .index(current.getIdx()).bonusRollsNum(current.getRollsToDo()).build();
    }

    private void validateState(int i, List<Integer> pins) {
        if (i <= 0 || i > NUM_FRAMES+1)
            throw new IndexOutOfBoundsException("Frame index "+ i+ " is out of allowed range [1,"+NUM_FRAMES+1+"]");

        if ( pins.size() != ROLLS_PER_FRAME)
            throw new IllegalArgumentException("Invalid number of rolls "+ pins.size());
    }

    private static boolean isBonusFrame(int i) {
        return i == NUM_FRAMES + 1;
    }

    /**
     *
     * @param player current player with it's state - the previous frames ,whose score are not finished yet
     * @param rollsToAdd number of rolls to consider in pinsDown list
     * @param pinsDown pins knocked down in the current frame
     * @return the first score, that was possible to finish from the queue with passed list of knocked pins
     */

    public int getRecentClosedScore(Player player, int rollsToAdd, List<Integer> pinsDown, long boardId) throws FrameClosedException {
        var firstClosedScore = 0;
        Iterator<Frame> it = player.getFramesQueue().iterator();
        Frame f;
        while(it.hasNext()){
            f = it.next();
            f.addPrevClosedScore(firstClosedScore);
            firstClosedScore = 0;
            f.closeScoreWithNextRoll(rollsToAdd, pinsDown);

            if (f.isClosed()) {
                player.addToFinalScoresLog(f.printScore());
                firstClosedScore = f.getScore();
                eventPublisher.publishEvent(new BoardUpdateEvent(mapToDto(f), false, player.getId(), boardId));
                it.remove();
            }
        }

        return firstClosedScore;
    }

}
