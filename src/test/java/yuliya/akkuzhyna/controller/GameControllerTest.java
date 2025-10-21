package yuliya.akkuzhyna.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import yuliya.akkuzhyna.MockConfig;
import yuliya.akkuzhyna.persistence.Player;
import yuliya.akkuzhyna.service.PlayerService;
import yuliya.akkuzhyna.service.ScoreBoardService;
import yuliya.akkuzhyna.service.ScoreKeepingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link GameController}
 */

@ExtendWith(SpringExtension.class)
@Import(MockConfig.class)
@WebMvcTest(controllers = GameController.class)
class GameControllerTest {
    @Autowired
    private ScoreKeepingService scoreKeepingService ;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ScoreBoardService boardService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    void getPlayedFramesScored() throws Exception {
        String req = """
                {
                    "frameIdx": 1,
                    "knockedPins": [5,5]
                }""";
        Player player = new Player();
        player.setId(1L);
        player.setName("Puh");
        when(playerService.findPlayer(anyLong())).thenReturn(player);
        doCallRealMethod().when(scoreKeepingService).addUpdateFrames(eq(1), same(List.of(5,5)),eq(1l),eq(0l));
        when(boardService.getUpdateBoard(anyLong(), anyLong())).thenCallRealMethod();
        mockMvc.perform(post("/bowling/frames/{boardId}/{userId}", 1L,0L)
                        .content(req)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getAllPlayers() throws Exception {
        mockMvc.perform(get("/bowling/players")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


}
