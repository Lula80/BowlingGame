package yuliya.akkuzhyna.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScoreReq {

    @Schema(
            name = "frameIdx",
            description = "current frame's index strting from 1, incremented by 1",
            requiredMode =  Schema.RequiredMode.REQUIRED,
            example = "1")
    private int frameIdx;
    @Schema(
            description = "list of  number os pins knocked down by each roll",
            example = "[8,1]")
    private List<Integer> knockedPins;
}
