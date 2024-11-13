package yuliya.akkuzhyna;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;
import yuliya.akkuzhyna.service.PlayerService;
import yuliya.akkuzhyna.service.ScoreBordService;
import yuliya.akkuzhyna.service.ScoreKeepingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        ReflectionTestUtils.setField(mock, ScoreKeepingService.Fields.bordService, bordService());
        return mock;
    }

    @Primary
    @Bean
    public ScoreBordService bordService() {
        ScoreBordService mock = Mockito.mock(ScoreBordService.class);
        ReflectionTestUtils.setField(mock, ScoreBordService.Fields.mapPlayerToFrames,
                new HashMap<Long, List<Integer>>());
        return mock;
    }

    @Primary
    @Bean
    public ApplicationEventPublisher eventPublisher() {
        return Mockito.mock(ApplicationEventPublisher.class);
    }
}