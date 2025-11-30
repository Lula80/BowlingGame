package yuliya.akkuzhyna.service;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import yuliya.akkuzhyna.dto.FrameDto;

@Getter
public class BoardUpdateEvent extends ApplicationEvent {
    private final FrameDto frame;
    private final boolean isNewFrame;
    private final Long playerId;
    private final long boardId;
    public BoardUpdateEvent(FrameDto state, boolean isNewFrame, long playerId, long boardId) {
        super(state);
        frame = state;
        this.isNewFrame = isNewFrame;
        this.playerId = playerId;
        this.boardId = boardId;
    }
}
