package ch.uzh.marugoto.backend.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.exception.DownloadNotebookException;
import ch.uzh.marugoto.core.exception.GameStateNotInitializedException;
import ch.uzh.marugoto.core.service.DownloadService;
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
    @Autowired
    private DownloadService downloadService;

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
    
    /**
     * Download compressed file with the notebook and uploaded files for a specific user
     * @return zip file student_files.zip
     * @throws DownloadNotebookException 
     * @throws FileNotFoundException 
     */
    @ApiOperation(value = "Download compressed file with the notebook and uploaded files for a specific user.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping(value = "files/{gameStateId}", produces = "application/zip")

    public ResponseEntity<InputStreamResource> downloadNotebookAndFilesForUser(@PathVariable String gameStateId, @RequestParam String userId) throws AuthenticationException, CreateZipException, CreatePdfException, DownloadNotebookException, FileNotFoundException {
    	
        FileInputStream zip =  downloadService.getCompressedFileForUserByGameState("gameState/"+ gameStateId, "user/" + userId);
        InputStreamResource streamResource = new InputStreamResource(zip);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=student_files.zip").body(streamResource);
    }
    
}
