package yuliya.akkuzhyna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import yuliya.akkuzhyna.dto.BoardDto;
import yuliya.akkuzhyna.dto.PlayerDto;
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

    @PostMapping(value = "/frames/{bordI}/{playerId}" , consumes = MediaType.APPLICATION_JSON_VALUE,  produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BoardDto>  scorePlayedFrames(@PathVariable("playerId") long playerId, @PathVariable("bordId") long bordId, @RequestBody ScoreReq req) throws PlayerNotFoundException, FrameClosedException;
}
