package yuliya.akkuzhyna.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import yuliya.akkuzhyna.dto.FrameDto;
import yuliya.akkuzhyna.dto.ScoreBord;
import yuliya.akkuzhyna.exception.FrameClosedException;

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

    private final ScoreBordService bordService;
    @Getter//for unit tests only
    private final List<Integer> finalScores = new ArrayList<>(NUM_FRAMES);
    //for intrenal use . keeps frames with not finished score
    private Queue<Frame> framesQueue = new LinkedList<>();

    public void resetBord(long playerId, int i){
        if(i==1 || i > NUM_FRAMES+1)
            bordService.resetBord(playerId);
    }

    /**
     * Overload or unit tests
     */
    public  List<FrameDto> getUpdatedFrames(int i, List<Integer> pins) throws FrameClosedException {
        return getUpdatedFrames(i,pins, 1L);
    }
    /**
     * @param i frames index
     * @param pins list of knocked down pins by roll of length ROLLS_PER_FRAME max
     *             Negative numbers will be classified as FOUL roll and will be reset to 0.
     * @return list of length  NUM_FRAMES  max with the current precumputed / updates scores for each frame played
     */
    public List<FrameDto> getUpdatedFrames(int i, List<Integer> pins, long userId) throws FrameClosedException {

       validateState(i, pins);
        var isBonusFrame = isBonusFrame(i);
        var pinsToDo= (isBonusFrame && !framesQueue.isEmpty())? getFinalRollsToDo() * PINS_PER_FRAME: PINS_PER_FRAME;
        var current = Frame.initFrame( i , pinsToDo, pins);

        current.addPrevClosedScore(
                    getRecentClosedScore(framesQueue, isBonusFrame?getFinalRollsToDo():current.getRollsMade(), current.getPinsDown(), userId));
        framesQueue.offer(current);

        if(!isBonusFrame)
            eventPublisher.publishEvent(new BordUpdateEvent( mapToDto(current), true, userId));

        return bordService.getJsonFrames(userId);
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

    private int getFinalRollsToDo() {
        return framesQueue.stream().mapToInt(Frame::getRollsToDo).sum();
    }

    /**
     * Needed for unit tests
     * @param framesQueue
     * @param current
     * @return
     */
    public int getLastClosedScore(Queue<Frame> framesQueue, Frame current) throws FrameClosedException {
        return getRecentClosedScore(framesQueue, current.getRollsMade(), current.getPinsDown(), 1L);
    }


    /**
     *
     * @param framesQueue the previous frames ,whose score are not finished yet
     * @param rollsToAdd number of rolls to consider in pinsDown list
     * @param pinsDown pins knocked down in the current frame
     * @return the first score, that was possible to finish from the queue with passed list of knocked pins
     */

    private int getRecentClosedScore(Queue<Frame> framesQueue, int rollsToAdd, List <Integer> pinsDown, long userId) throws FrameClosedException {
        var firstClosedScore = 0;
        Iterator<Frame> it = framesQueue.iterator();
        Frame f;
        while(it.hasNext()){
            f = it.next();
            f.addPrevClosedScore(firstClosedScore);
            firstClosedScore = 0;
            f.closeScoreWithNextRoll(rollsToAdd, pinsDown);

            if (f.isClosed()) {
                finalScores.add(f.printScore());
                firstClosedScore = f.getScore();
                eventPublisher.publishEvent(new BordUpdateEvent(mapToDto(f), false, userId));

                it.remove();
            }
        }

        return firstClosedScore;
    }

    public ScoreBord calculateTotalScore(String user, Long userId){
      return bordService.calculateTotalScore(user, userId);
    }
}
