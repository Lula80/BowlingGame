package yuliya.akkuzhyna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * API's response about user's played frames
 */
import java.util.List;
@AllArgsConstructor
@Data
public class ScoreBord {
    String userName;
    int totalScore;
    List<FrameDto> frames;
}
