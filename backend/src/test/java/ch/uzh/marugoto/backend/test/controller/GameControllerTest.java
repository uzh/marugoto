package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.scheduling.annotation.Async;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;

@AutoConfigureMockMvc
public class GameControllerTest extends BaseControllerTest {

    @Autowired
    private GameStateRepository gameStateRepository;

    @Test
    public void testListGameStates() throws Exception {
        var openGames = gameStateRepository.findNotFinishedStates(user.getId());
        var finishedGames = gameStateRepository.findFinishedStates(user.getId());
        var classroomGames = gameStateRepository.findClassroomNotFinishedStates(user.getId());

        mvc.perform(authenticate(get("/api/game/list")))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openGames", hasSize(openGames.size())))
            .andExpect(jsonPath("$.finishedGames", hasSize(finishedGames.size())))
            .andExpect(jsonPath("$.classroomGames", hasSize(classroomGames.size())));
    }

    @Test
    @Async
    public void testContinueGame() throws Exception {
        var gameState = user.getCurrentGameState();

        mvc.perform(authenticate(put("/api/game/continue/" + gameState.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        user = userRepository.findByMail("unittest@marugoto.ch");
        assertEquals(gameState.getId(), user.getCurrentGameState().getId());
    }

    @Test
    public void testDownloadNotebookAndFilesForUser() throws Exception {
        var gameStateId = user.getCurrentGameState().getId();
        gameStateId = gameStateId.replaceAll("[^0-9]","");

        mvc.perform(authenticate(get("/api/game/files/" + gameStateId).accept("application/zip")))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
