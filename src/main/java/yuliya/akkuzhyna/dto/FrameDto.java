package yuliya.akkuzhyna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * played frame status for API's response
 */
@Builder
@Getter
@AllArgsConstructor
@ToString
public class FrameDto {
    @Schema(
            description = "frame's index from request",
            example = "1")
    final int index;
    @Schema(
            name = "score",
            description = "current frame without bonus rolls score added",
            requiredMode =  Schema.RequiredMode.REQUIRED,
            example = "1")
    @Setter
    int score;
    @Schema(
            name = "bonus rolls",
            description = "number of bonus rolls needed to complete the score of the frame",
            example = "1")
    final int bonusRollsNum;
    @Setter
    @Schema(
            description = "boolean value indicating, whther the score of the frame complete- bonus rolls are used ",
            example = "false")
    boolean closed;
}
