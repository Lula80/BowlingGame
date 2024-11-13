package yuliya.akkuzhyna.service;

import lombok.Getter;

import static yuliya.akkuzhyna.utils.Constants.ROLLS_PER_FRAME;

/**
 * the state of the frame regarding its scoring strategy.
 * Contains information about number of bonus rolls, needed to
 * finish this type of frame
 * and number of rolls to take into account to score this type of
 * frame out of total number of rolls done
 */
public enum BonusEnum {
    STRIKE(ROLLS_PER_FRAME, 1),
    SPARE(1, ROLLS_PER_FRAME),
    FOUL(0, ROLLS_PER_FRAME),
    MISS(0, ROLLS_PER_FRAME),
    NORMAL(0, ROLLS_PER_FRAME);

    private final int bonusRolls;
    @Getter
    private final int rollsMade;

    BonusEnum(int toDo, int done) {
        bonusRolls = toDo;
        rollsMade = done;
    }

    int getBonusRolls(){
        return  bonusRolls;
    }

}
