package yuliya.akkuzhyna;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import yuliya.akkuzhyna.dto.FrameDto;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.service.Frame;
import yuliya.akkuzhyna.service.ScoreBordService;
import yuliya.akkuzhyna.service.ScoreKeepingService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static yuliya.akkuzhyna.utils.Constants.*;

@SpringBootTest(classes={ScoreKeepingService.class, ScoreBordService.class, ApplicationEventPublisher.class})
class ScoreKeepingServiceIT {

    private List<Integer> result = new ArrayList<>(12);
    private List<FrameDto> frames = new ArrayList<>(12);
    @Autowired
    private  ScoreBordService bordService;
    @Autowired
    private  ApplicationEventPublisher eventPublisher;
    private final ScoreKeepingService scoringService = new ScoreKeepingService(eventPublisher, bordService);


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scoringService, ScoreKeepingService.Fields.bordService, bordService);
        ReflectionTestUtils.setField(scoringService, ScoreKeepingService.Fields.eventPublisher, eventPublisher);
        bordService.resetBord(1L);
        result.clear();
    }

    @Test
    void testImmutableFrameState() {
        Frame f = Frame.initFrame(1, 10, List.of(2, 5));
        assertThrows(UnsupportedOperationException.class, () -> f.getPinsDown().add(7));
    }

    @Test
    void testInvalidFrameState() {
        assertThrows(IllegalArgumentException.class, () -> Frame.initFrame(1, 10, List.of(6, 5)));
    }

    @Test
    void testInvalidFrameState2() {
        assertThrows(IndexOutOfBoundsException.class, () -> scoringService.getUpdatedFrames(13, List.of(3, 5)));
    }

    @Test
    void mixedFrames() throws FrameClosedException {
        List<Integer> pins = List.of(10, 0, 10, 0, 1, 9, 2, 2, 3, 3, 5, 5, 10, 0, 10, 0, 10, 0, 7, 3, 10, 0);
        List<Integer> expected = List.of(0, 0, 41, 57, 63,63, 83,83, 113, 160, 180);
        var i = testRollsInLoop(pins, expected);
        assertFalse(frames.getLast().isClosed());
        testBonusFrame(i / ROLLS_PER_FRAME + 1, pins.subList(i, i + ROLLS_PER_FRAME), expected);
        assertEquals(List.of(21, 41, 53, 57, 63, 83, 113, 140, 160, 180), scoringService.getFinalScores());

    }

    @Test
    void mixedFrames1() throws FrameClosedException {
        List<Integer> pins = List.of(9, 1, 10, 0, 5, 4, 7, 3, 10, 0, 10, 0, 9, 0, 6, 4, 10, 0, 8, 2, 10, 0);
        List<Integer> expected = List.of(0, 20, 48, 48, 68, 68, 125, 125, 145, 165, 185);
        var i = testRollsInLoop(pins, expected);
        assertFalse(frames.getLast().isClosed());
        testBonusFrame(i / ROLLS_PER_FRAME + 1, pins.subList(i, i + ROLLS_PER_FRAME), expected);
        assertEquals(List.of(20, 39, 48, 68, 97, 116, 125, 145, 165, 185), scoringService.getFinalScores());
    }

   @Test
    void oneFoul() throws FrameClosedException {
        var pins = List.of(10,0, 10,0, -1,9, 2,2, 3,3, 5,5, 10,0 , 10,0, 10,0, 10,0,    10,10);
       List<Integer> expectedTotals = List.of(0, 0, 48, 52, 58, 58, 78, 78, 108, 138, 198);
       var i = testRollsInLoop(pins, expectedTotals);
       assertFalse(frames.getLast().isClosed());
       testBonusFrame(i / ROLLS_PER_FRAME + 1, pins.subList(i, i + ROLLS_PER_FRAME), expectedTotals);
        assertEquals( List.of(20, 39, 48, 52, 58, 78, 108, 138, 168, 198), scoringService.getFinalScores());
    }

    @Test
    void testPerfectGame() throws FrameClosedException {
        var pins= List.of(10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,0 , 10,0, 10,0,   10,0,10,10);
        List<Integer> expectedTotals = List.of(0, 0, 30, 60, 90, 120, 150, 180,210, 240, 300);
        var i = testRollsInLoop(pins, expectedTotals);
        assertFalse(frames.getLast().isClosed());
       //test last frame with bonus rolls
        testBonusFrame(i / ROLLS_PER_FRAME +1, pins.subList(i, pins.size()),expectedTotals);
        assertEquals(List.of(30, 60, 90, 120, 150, 180, 210, 240, 270, 300), scoringService.getFinalScores());
    }

    @Test
    void testPerfectGameWithLastFoul() throws FrameClosedException {
        var pins = List.of(10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,0 , 10,0, 10,0, 10,0, 10,-10);
        List<Integer> expectedTotals =  List.of(0, 0, 30, 60, 90, 120, 150, 180,210, 240, 290);
        var i = testRollsInLoop(pins, expectedTotals);
        assertFalse(frames.getLast().isClosed());
        testBonusFrame(i / ROLLS_PER_FRAME + 1, pins.subList(i, i + ROLLS_PER_FRAME), expectedTotals);
        assertEquals(List.of(30, 60, 90, 120, 150, 180, 210, 240, 270, 290), scoringService.getFinalScores());
    }

    @Test
    void testRandoms() throws FrameClosedException {
        var input = generateRandomRolls(NUM_FRAMES+1, PINS_PER_FRAME +1 );
        System.out.println("Input: "+ input.stream().map(String::valueOf).collect(Collectors.joining(",")));
        var rollsNum = NUM_FRAMES * ROLLS_PER_FRAME;
        int i;
        for (i=0; i < rollsNum; i += ROLLS_PER_FRAME) {
            frames = scoringService.getUpdatedFrames(i / ROLLS_PER_FRAME + 1, input.subList(i, i + ROLLS_PER_FRAME));
            var bord = bordService.calculateTotalScore("Test player", 1L);
            System.out.println("Bord "+ bord);
        }

        if(!frames.getLast().isClosed()){
            frames = scoringService.getUpdatedFrames(i / ROLLS_PER_FRAME + 1, input.subList(i, i + ROLLS_PER_FRAME));
            var bord = bordService.calculateTotalScore("Test player", 1L);
            System.out.println("Bord "+ bord);
        }


        assertEquals(expectedScore(input.subList(0,6)), scoringService.getFinalScores().get(0) );
        assertEquals(scoringService.getFinalScores().get(0) + expectedScore(input.subList(2,8)),scoringService.getFinalScores().get(1));
        assertEquals( scoringService.getFinalScores().get(6)+expectedScore(input.subList(14,20)), scoringService.getFinalScores().get(7));
    }

    /**
     * @param size length of generated numbers
     * @param  upperBound
     * @return list of random number ,each less than upperBound
     * each pair of numbers is in total less than 10, last 11th pair is to be used for bonus frame
     */
    private List<Integer> generateRandomRolls( int size,int upperBound){
        IntStream rolls = new Random().ints(size, 0, upperBound);
       List<Integer> ret = rolls.flatMap( r -> IntStream.of(r, new Random().nextInt(upperBound-r)))
                .boxed().collect(toList());
       //correct bonus frame, if last frame -spare
       if(ret.get(size-1)+ ret.get(size-2) == upperBound-1)
           ret.set(size-1,0);
       return ret;
    }

    /**
     * Tests 10 frames
     * @param pins input of knocked pins. Only first 20 will be tested
     * @param expected list of total scores on scoring vord
     * @return index if the last frame* rolls per frame
     */
    private int testRollsInLoop(List<Integer> pins, List<Integer> expected) throws FrameClosedException {
        var i = 0;
        var rollsNum =NUM_FRAMES * ROLLS_PER_FRAME;
        for (i = 0; i < rollsNum; i += ROLLS_PER_FRAME) {
            testOneFrame(i / ROLLS_PER_FRAME + 1,  pins.subList(i, i + ROLLS_PER_FRAME), expected);
        }
        return i;
    }

    /**
     * @param i index of new frame to add to scoring bord , ranging  from 1 to 10
     * @param pins knocked in the frame
     * @param expected total scores
     */
    private void testOneFrame(int i, List<Integer> pins, List<Integer> expected) throws FrameClosedException {
        frames = scoringService.getUpdatedFrames(i, pins);
        var bord = bordService.calculateTotalScore("Test player", 1L);
        //bonus frame will not be added to bord, it's score will be added to the last frames, frames list will not grow longer than NUM_FRAMES
        assertThat(frames).hasSize(i);
        assertThat(bord.getFrames()).hasSize(i);

        System.out.println("bord total "+i+" "+bord.getTotalScore());
        assertThat(bord.getTotalScore()).isEqualTo(expected.get(i-1));

    }

    /**
     *
     * @param i index of bonus frame. Must be equal to 11
     * @param pins knocked in the frame
     * @param expected total scores
     */
    private void testBonusFrame(int i, List<Integer> pins, List<Integer> expected) throws FrameClosedException {
        frames = scoringService.getUpdatedFrames(i, pins);
        var bord = bordService.calculateTotalScore("Test player", 1L);
        //bonus frame will not be added to bord, it's score will be added to the last frames, frames list will not grow longer than NUM_FRAMES
        assertThat(frames).hasSize(NUM_FRAMES);
        assertThat(bord.getFrames()).hasSize(NUM_FRAMES);

        System.out.println("bord total "+i+" "+bord.getTotalScore());
        assertThat(bord.getTotalScore()).isEqualTo(expected.get(i-1));

    }

    private int expectedScore(List<Integer> pinsDown){
        if(pinsDown.get(0) == PINS_PER_FRAME && pinsDown.get(2) == PINS_PER_FRAME)
            return PINS_PER_FRAME + PINS_PER_FRAME + pinsDown.get(4);
        if(pinsDown.get(0) == PINS_PER_FRAME){
            return pinsDown.get(2) == PINS_PER_FRAME ? expectedScore(pinsDown.subList(2,6)) + PINS_PER_FRAME :
                    PINS_PER_FRAME + pinsDown.get(2) + pinsDown.get(3);
        }
        //also HARD SPARE->
        if(pinsDown.get(0)+pinsDown.get(1) == PINS_PER_FRAME){
            return PINS_PER_FRAME + pinsDown.get(2);
        }
        return pinsDown.get(0) + pinsDown.get(1);
    }


}