package yuliya.akkuzhyna.service;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import yuliya.akkuzhyna.dto.FrameDto;

@Getter
public class BordUpdateEvent extends ApplicationEvent {
    private FrameDto frame;
    private final boolean isNewFrame;
    private final Long playerId;
    public BordUpdateEvent(FrameDto current, boolean isNewFrame, long userId) {
        super(current);
        frame = current;
        this.isNewFrame = isNewFrame;
        this.playerId = userId;
    }
}
