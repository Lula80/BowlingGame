package yuliya.akkuzhyna;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;
import yuliya.akkuzhyna.service.PlayerService;
import yuliya.akkuzhyna.service.ScoreBoardService;
import yuliya.akkuzhyna.service.ScoreKeepingService;

import java.util.ArrayList;
import java.util.HashMap;

@TestConfiguration
public class MockConfig {

    @Primary
    @Bean
    public PlayerService playerService() {
        return Mockito.mock(PlayerService.class);
    }
    @Primary
    @Bean
    public ScoreKeepingService scoreKeepingService() {
        ScoreKeepingService mock = Mockito.mock(ScoreKeepingService.class);
        ReflectionTestUtils.setField(mock, ScoreKeepingService.Fields.finalScores, new ArrayList<Integer>());
        return mock;
    }

    @Primary
    @Bean
    public ScoreBoardService boardService() {
        ScoreBoardService mock = Mockito.mock(ScoreBoardService.class);
        ReflectionTestUtils.setField(mock, ScoreBoardService.Fields.boards, HashMap.newHashMap(1));//.mapPlayerToFrames, new HashMap<Long, List<Integer>>());
        return mock;
    }

    @Primary
    @Bean
    public ApplicationEventPublisher eventPublisher() {
        return Mockito.mock(ApplicationEventPublisher.class);
    }
}