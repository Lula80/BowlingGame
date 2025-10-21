package yuliya.akkuzhyna.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuliya.akkuzhyna.dto.BoardDto;
import yuliya.akkuzhyna.dto.PlayerDto;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.exception.PlayerNotFoundException;
import yuliya.akkuzhyna.service.PlayerService;
import yuliya.akkuzhyna.service.ScoreBoardService;
import yuliya.akkuzhyna.service.ScoreKeepingService;


import java.util.List;

@RestController
@RequestMapping(value = "/bowling")
@RequiredArgsConstructor
@Slf4j
public final class GameController implements GameApi {

    private final ScoreKeepingService scoreKeepingService;

    private  final ScoreBoardService bordService;

    private final PlayerService playerService;

    @PostMapping(value = "/frames/{bordId}/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<BoardDto>  scorePlayedFrames(@PathVariable("userId") long userId, @PathVariable("bordId") long boardId, @RequestBody ScoreReq req) throws PlayerNotFoundException, FrameClosedException {

        var player = playerService.findPlayer(userId);
        bordService.resetBoard(boardId, userId, req.getFrameIdx());

        scoreKeepingService.addUpdateFrames(req.getFrameIdx(), req.getKnockedPins(), player, boardId);
        return ResponseEntity.ok().body(bordService.getUpdatedBoard( player.getId(), boardId, player.getName()));
    }
//to move to AdminController
    @Override
    @GetMapping(value = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlayerDto>> getAllPlayers(){
        List<PlayerDto> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }
}
