package yuliya.akkuzhyna.service;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import yuliya.akkuzhyna.dto.BoardDto;
import yuliya.akkuzhyna.dto.FrameDto;

import java.util.*;

import static yuliya.akkuzhyna.utils.Constants.NUM_BOARDS;
import static yuliya.akkuzhyna.utils.Constants.NUM_FRAMES;

@Service
@FieldNameConstants
public class ScoreBoardService implements ApplicationListener<BoardUpdateEvent> {

    private Map<Long,ScoreBoard> boards;


    private ScoreBoardService(){
        boards = HashMap.newHashMap(NUM_BOARDS);
        for (long i = 0; i < NUM_BOARDS; i++) {
            boards.put(i, new ScoreBoard(i));
        }
    }

    /**
     *  processes two types of events:
     *  1. new frame
     *  2. re-score previous frame
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(BoardUpdateEvent event) {
        if (event.isNewFrame())
           boards.get(event.getBoardId()).addNewFrame(event.getPlayerId(), event.getFrame());
        else {
            boards.get(event.getBoardId()).updatePrevFrameOnBord(event.getFrame(), event.getPlayerId());
        }
        if(event.getFrame().isClosed())
              boards. get(event.getBoardId()).updateCurrentClosedCore (event.getPlayerId(), event.getFrame().getScore());
    }

    /**
     *
     * @param playerId
     * @param boardId
     * @return show player , score, frames
     */
    public BoardDto getUpdatedBoard(Long playerId, Long boardId , String playerName) {
        return new BoardDto(boards.get(boardId).getTotalScore(playerId), boards.get(boardId).getFrames(playerId), playerName);
    }

    /**
     * @param boardId
     * @param playerId
     * @return list of length  NUM_FRAMES  max with the current precomputed / updated scores for each frame played
     */
    public List<FrameDto> getJsonFrames( long boardId, long playerId) {
        return boards.get(boardId).getFrames(playerId);
    }

    public int getTotalScore(long boardId,long playerId) {
        return  boards.get(boardId).getTotalScore(playerId);
    }

    public void resetBoard(long bordId, long playerId,  int frameId){
        if(frameId==1 || frameId > NUM_FRAMES+1) {
           ScoreBoard board = boards.get(bordId);
            board.clear(playerId);
        }
    }
}