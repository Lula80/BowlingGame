package yuliya.akkuzhyna;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.service.BordUpdateEvent;
import yuliya.akkuzhyna.service.Frame;
import yuliya.akkuzhyna.service.ScoreBordService;
import yuliya.akkuzhyna.service.ScoreKeepingService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;


class ScoreKeepingServiceTest {
    @Autowired
    private  ApplicationEventPublisher eventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    private final ScoreKeepingService scoringService = new ScoreKeepingService(eventPublisher, Mockito.mock(ScoreBordService.class));

    @BeforeEach
    void setUp() {
       doNothing().when(eventPublisher).publishEvent(BordUpdateEvent.class);
        ReflectionTestUtils.setField(scoringService, ScoreKeepingService.Fields.eventPublisher, eventPublisher);

    }

    @Test
    void  getLastClosedScore() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateStrike();
        Frame f2 = generateStrike();
        f.setScore(10 + f2.getRollsMade()*10);
        framesQueue.add(f);
        framesQueue.add(f2);
        framesQueue.element().decrementRollsToDo();

        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateSpare());
        assertEquals( 41, lastClosed);
        assertEquals(framesQueue.size(), 0);
    }

    @Test
    void  getLastClosedScoreStrike() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateStrike();
        Frame f2 = generateStrike();
        f.setScore(10 + f2.getRollsMade() * 10);
        framesQueue.add(f);
        framesQueue.add(f2);
        framesQueue.element().decrementRollsToDo();
        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateStrike());
        assertEquals( 0, lastClosed);
        assertEquals( 1, framesQueue.size());
        assertEquals(1, framesQueue.element().getRollsToDo());
        assertEquals(50, framesQueue.element().getScore());
    }

    @Test
    void  getLastClosedScoreMiss() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateStrike();
        Frame f2 = generateStrike();
        f.setScore(10+f2.getRollsMade()*10);
        framesQueue.add(f);
        framesQueue.add(f2);
        framesQueue.element().decrementRollsToDo();
        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateMiss());
        assertEquals( 30, lastClosed);
        assertEquals(0, framesQueue.size());
    }

    @Test
    void  getLastClosedScoreNormal() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateStrike();
        Frame f2 = generateStrike();
        f.setScore(10+f2.getRollsMade()*10);
        framesQueue.add(f);
        framesQueue.add(f2);
        framesQueue.element().decrementRollsToDo();
        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateNormal());
        assertEquals( 33, lastClosed);
        assertEquals(0, framesQueue.size());
    }

    @Test
    void  getLastClosedScoreNormal1() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateSpare();
        framesQueue.add(f);
        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateNormal());//1, 1);
        assertEquals( 11, lastClosed);
        assertEquals(0, framesQueue.size());
    }

    @Test
    void  getLastClosedScoreFoul() throws FrameClosedException {
        Queue<Frame> framesQueue = new LinkedList<>();
        Frame f = generateSpare();
        framesQueue.add(f);
        int lastClosed = scoringService.getLastClosedScore(framesQueue, generateFoul());
        assertEquals( 10, lastClosed);
        assertEquals(0, framesQueue.size());
    }


    private Frame generateStrike(){
        Frame f1 = Frame.initFrame(1,10, List.of(10,0));
        f1.setScore(10);
        return f1;

    }

    private Frame generateSpare(){
        Frame f1 = Frame.initFrame(1, 10, List.of(1,9));
        f1.setScore(10);
        return f1;
    }

    private Frame generateNormal(){
        Frame f1 = Frame.initFrame(1, 10, List.of(1,1));
        f1.setScore(2);
        return f1;
    }

    private Frame generateMiss(){
        return Frame.initFrame(1,10, List.of(0,0));
    }

    private Frame generateFoul(){
        return Frame.initFrame(1, 10, List.of(-7,3));
    }
}