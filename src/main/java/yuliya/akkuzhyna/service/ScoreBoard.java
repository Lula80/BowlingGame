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
 * API's response about user's played frames
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

    public void clear(long userId) {
        mapPlayerToFrames.clear();
        mapPlayerToFrames.put(userId, new ArrayList<>(Constants.NUM_FRAMES));
        mapPlayerToScore.clear();
    }

    public void updatePrevFrameOnBord(FrameDto f, Long userId) {
        FrameDto frameDto = mapPlayerToFrames.get(userId).get(f.getIndex()-1);
        frameDto.setClosed(f.isClosed());
        frameDto.setScore(f.getScore());
    }

    public int getTotalScore( long userId) {
        return mapPlayerToScore.getOrDefault(userId,0);
    }

    public List<FrameDto> getFrames(Long userId) {
       return mapPlayerToFrames.get(userId);
    }

    public void updateCurrentClosedCore(Long userId, int score) {
        mapPlayerToScore.put(userId, score);
    }
}
