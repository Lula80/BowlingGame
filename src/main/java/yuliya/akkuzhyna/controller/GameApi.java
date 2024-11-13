package yuliya.akkuzhyna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import yuliya.akkuzhyna.dto.PlayerDto;
import yuliya.akkuzhyna.dto.ScoreBord;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.exception.PlayerNotFoundException;


import java.util.List;

@Tag(name = "Bowling", description = "Bowling Game Api")
sealed interface GameApi permits GameController{


    @Operation(
            summary = "Fetch all players",
            description = "fetches all players entities and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    ResponseEntity<List<PlayerDto>> getAllPlayers();

    @Operation(
            summary = "calculates score for the frames passen in request",
            description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully added score"),
            @ApiResponse(responseCode = "404", description = "player is not found in database")
    })
    ResponseEntity<ScoreBord> scorePlayedFrames(@Parameter(name = "userId", example = "1") long userId,
                                                @RequestBody(required = true, description = "request data for score computation") ScoreReq req)
            throws PlayerNotFoundException, FrameClosedException;
}
