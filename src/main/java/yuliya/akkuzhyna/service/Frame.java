package yuliya.akkuzhyna.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import yuliya.akkuzhyna.exception.FrameClosedException;

import java.util.List;
import java.util.stream.Collectors;

import static yuliya.akkuzhyna.utils.Constants.*;

@Slf4j
public class Frame {

    @Setter
    @Getter
    private int score = 0;
    @Getter
    private int rollsToDo;
    @Getter
    private final int idx;
    @Getter
    private int rollsMade;
    @Getter
    private List<Integer> pinsDown;
    private BonusEnum bonusState;

    private Frame(int idx){
        this.idx = idx;
        this.setBonusState(BonusEnum.NORMAL);
    }

    /**
     *
     * @param idx index of the frame from 1 to 10
     *  @param pinsToDo the maximum number of rolls available for this frame
     * @param pins list of knocked down pins in each roll, th length is not constant, because of last frame getting bonus rolls
     * @return frame with initial information set
     */
    public static Frame initFrame(int idx, int pinsToDo,  List<Integer> pins) {
        areValidRolls(pins);
        Frame current = new Frame(idx);
        current.computeBonusStatePreliminaryScore(pinsToDo, pins);
        return current;
    }

    private static void areValidRolls(List<Integer> pins) {
        pins.stream().filter(p -> PINS_PER_FRAME - Math.abs(p) >  PINS_PER_FRAME).findAny()
                .ifPresent(p -> { throw new IllegalArgumentException("Number of knoked pins by one roll "+p+ " is out of range.");});
    }
    /**
     *
     * @param pinsToDo the maximum number of rolls available for this frame
     * @param pinsDown list of knocked down pins in each roll
     */
    void computeBonusStatePreliminaryScore(int pinsToDo, List<Integer> pinsDown){
        this.setPinsDownAndScore(pinsDown);

        if (this.score > pinsToDo)
            throw new IllegalArgumentException("There are more pins than allowed in the frame " + this.score+ ">"+ pinsToDo+" "+ this.idx);

        if(this.score == 0)
            this.setBonusState(BonusEnum.MISS);
        if(this.pinsDown.contains(0))
            this.setBonusState(BonusEnum.FOUL);
        if(this.score == pinsToDo)
            this.setBonusState(BonusEnum.SPARE);
        if(this.pinsDown.getFirst() == PINS_PER_FRAME)
            this.setBonusState(BonusEnum.STRIKE);

    }

    public void setPinsDownAndScore(List<Integer> pins) {
        this.pinsDown = pins.stream().limit(ROLLS_PER_FRAME).map(this::updateScoreFoulRoll).toList();
    }

    public boolean isClosed(){
        return getRollsToDo() == 0;
    }

    private void setBonusState(BonusEnum state){
        this.bonusState = state;
        this.rollsToDo = bonusState.getBonusRolls();
        this.rollsMade = bonusState.getRollsMade();
    }

    private Integer updateScoreFoulRoll(Integer pinsDown){
            if(pinsDown < 0){
               return 0;
            }
            this.score+=pinsDown;
            return pinsDown;
    }

    public void closeScoreWithNextRoll(int rollsToAdd, List<Integer> pinsDown) throws FrameClosedException {
        int i = 0;
        while(getRollsToDo() > 0 && i < rollsToAdd){
            decrementRollsToDo();
            score += pinsDown.get(i++);
        }
    }

    public void decrementRollsToDo() throws FrameClosedException {
        if (this.rollsToDo == 0)
            throw new FrameClosedException("the frame has already the score from future rolls added.");
        this.rollsToDo--;
    }

    public void addPrevClosedScore(int prevFinalScore){
        this.score += prevFinalScore;
    }

    /***
     * logs the frame's state
     * @return current state of finalScore
     */
    public int printScore() {
        log.info(
                this.idx + ". " +
                        pinsDown.stream()
                                .map(String::valueOf).collect(Collectors.joining("|")) +
                        " final score: " + score + " frame type : " + bonusState.name());
        return score;
    }

    @Override
    public String toString() {
        return   this.idx+" pinsDown = "+pinsDown;
    }
}
