package yuliya.akkuzhyna.service;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import yuliya.akkuzhyna.dto.FrameDto;
import yuliya.akkuzhyna.dto.ScoreBord;
import yuliya.akkuzhyna.utils.Constants;

import java.util.*;

@Service
@FieldNameConstants
public class ScoreBordService implements ApplicationListener<BordUpdateEvent> {
    private Map<Long, List<FrameDto>> mapPlayerToFrames = new HashMap<>();

    @Getter
    private Map<Long, Integer> mapPlayerToScore = new HashMap<>();

    @Override
    public void onApplicationEvent(BordUpdateEvent event) {
        if (event.isNewFrame())
            mapPlayerToFrames.get(event.getPlayerId()).add(mapToDto(event.getFrame()));
        else {
            updateClosedFrameOnBord(event.getFrame(), event.getPlayerId());
        }
        if(event.getFrame().isClosed())
            mapPlayerToScore.put(event.getPlayerId(), event.getFrame().getScore());
    }

    private void updateClosedFrameOnBord( Frame f, Long id){
        FrameDto frameDto = mapPlayerToFrames.get(id).get(f.getIdx()-1);
        frameDto.setClosed(f.isClosed());
        frameDto.setScore(f.getScore());
    }

    public ScoreBord calculateTotalScore(String name, Long userId ) {
        return new ScoreBord(name, mapPlayerToScore.getOrDefault(userId, 0), mapPlayerToFrames.get(userId));
    }

    public List<FrameDto> getJsonFrames( long id) {
        return mapPlayerToFrames.get(id);
    }

    public int getTotalScore( long id) {
        return mapPlayerToScore.get(id);
    }


    private static FrameDto mapToDto(Frame current) {
        return FrameDto.builder().score(current.getScore()).closed(current.isClosed())
                .index(current.getIdx()).bonusRollsNum(current.getRollsToDo()).build();
    }
    public void resetBord(long userId){
        mapPlayerToFrames.clear();
        mapPlayerToFrames.put(userId, new ArrayList<>(Constants.NUM_FRAMES));
        mapPlayerToScore.clear();
    }
}