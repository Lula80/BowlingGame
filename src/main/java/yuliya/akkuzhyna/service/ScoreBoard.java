package yuliya.akkuzhyna.service;

import lombok.Data;
import lombok.Getter;
import yuliya.akkuzhyna.dto.FrameDto;
import yuliya.akkuzhyna.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * API's response about player's played frames
 */
@Data
public class ScoreBoard {
    long id;
    @Getter
    private Map<Long, List<FrameDto>> mapPlayerToFrames = new HashMap<>();
    @Getter//players current closed score
    private Map<Long, Integer> mapPlayerToScore = new HashMap<>();

    public ScoreBoard(long id){
        this.id = id;
    }
    boolean addNewFrame(long playerId, FrameDto newFrame){
       return mapPlayerToFrames.get(playerId).add(newFrame);
    }

    public void clear(long playerId) {
        mapPlayerToFrames.clear();
        mapPlayerToFrames.put(playerId, new ArrayList<>(Constants.NUM_FRAMES));
        mapPlayerToScore.clear();
    }

    public void updatePrevFrameOnBord(FrameDto f, Long playerId) {
        FrameDto frameDto = mapPlayerToFrames.get(playerId).get(f.getIndex()-1);
        frameDto.setClosed(f.isClosed());
        frameDto.setScore(f.getScore());
    }

    public int getTotalScore( long playerId) {
        return mapPlayerToScore.getOrDefault(playerId,0);
    }

    public List<FrameDto> getFrames(Long playerId) {
       return mapPlayerToFrames.get(playerId);
    }

    public void updateCurrentClosedCore(Long playerId, int score) {
        mapPlayerToScore.put(playerId, score);
    }
}
