package yuliya.akkuzhyna.service;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class BordUpdateEvent extends ApplicationEvent {
    private Frame frame;
    private final boolean isNewFrame;
    private final Long playerId;
    public BordUpdateEvent(Frame current, boolean isNewFrame, long userId) {
        super(current);
        frame = current;
        this.isNewFrame = isNewFrame;
        this.playerId = userId;
    }
}
