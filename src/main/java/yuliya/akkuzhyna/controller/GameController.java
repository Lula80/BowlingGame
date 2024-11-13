package yuliya.akkuzhyna.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuliya.akkuzhyna.dto.PlayerDto;
import yuliya.akkuzhyna.dto.ScoreBord;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.exception.PlayerNotFoundException;
import yuliya.akkuzhyna.service.PlayerService;
import yuliya.akkuzhyna.service.ScoreKeepingService;


import java.util.List;

@RestController
@RequestMapping(value = "/bowling")
@RequiredArgsConstructor
@Slf4j
public final class GameController implements GameApi {

    private final ScoreKeepingService scoreKeepingService;

    private final PlayerService playerService;

    @Override
    @PostMapping(value = "/frames/{userId}" , consumes = MediaType.APPLICATION_JSON_VALUE,  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScoreBord>  scorePlayedFrames(@PathVariable("userId") long userId, @RequestBody ScoreReq req) throws PlayerNotFoundException, FrameClosedException {
       scoreKeepingService.resetBord(userId, req.getFrameIdx());
        var player = playerService.findPlayer(userId);
       scoreKeepingService.getUpdatedFrames(req.getFrameIdx(), req.getKnockedPins(), userId);

       return ResponseEntity.ok().body(scoreKeepingService.calculateTotalScore(player.getName(), player.getId()));
    }
//to move to AdminController
    @Override
    @GetMapping(value = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlayerDto>> getAllPlayers(){
        List<PlayerDto> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }
}
