package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.exception.GameStateNotInitializedException;
import ch.uzh.marugoto.core.service.GameStateService;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/game")
public class GameController extends BaseController {

    @Autowired
    private GameStateService gameStateService;
    @Autowired
    private StateService stateService;

    /**
     * List all games for user
     * @return games list
     */
    @ApiOperation(value = "List all games for authenticated user", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public HashMap<String, List<GameState>> listGames() throws AuthenticationException {
        User user = getAuthenticatedUser();
        HashMap<String, List<GameState>> games = new HashMap<>();
        games.put("classroomGames", gameStateService.getClassroomGames(user));
        games.put("openGames", gameStateService.getOpenGames(user));
        games.put("finishedGames", gameStateService.getFinishedGames(user));
        return games;
    }

    /**
     * Continue specific game
     * @return states
     */
    @ApiOperation(value = "Continue specific game by gameState ID", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "continue/gameState/{gameStateId}", method = RequestMethod.PUT)
    public HashMap<String, Object> continueGame(@PathVariable String gameStateId) throws AuthenticationException, GameStateNotInitializedException {
        User user = getAuthenticatedUser();
        gameStateService.setGameState("gameState/".concat(gameStateId), user);
        return stateService.getStates(user);
    }
}
