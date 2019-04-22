package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class GameStateService {

	@Autowired
	private GameStateRepository gameStateRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private PageStateRepository pageStateRepository;

	/**
	 * Return all classroom specific games
	 *
	 * @param user authenticated user
	 * @return gamesList
	 */
	public List<GameState> getClassroomGames(User user) {
		return gameStateRepository.findClassroomNotFinishedStates(user.getId());
	}

	/**
	 * Return all not finished games
	 *
	 * @param user authenticated user
	 * @return gamesList
	 */
	public List<GameState> getOpenGames(User user) {
		return gameStateRepository.findNotFinishedStates(user.getId());
	}

	/**
	 * Return all finished games for user
	 *
	 * @param user authenticated user
	 * @return gamesList
	 */
	public List<GameState> getFinishedGames(User user) {
		return gameStateRepository.findFinishedStates(user.getId());
	}

	/**
	 * Initialize or finish topic state if one topic is started
	 *
	 * @param user authenticated user
	 * @return void
	 */
	public GameState initializeState(User user, Topic topic) {

		GameState gameState = new GameState(topic);
		gameState.setUser(user);
		save(gameState);

		userService.updateGameState(user, gameState);
		userService.updatePageState(user,pageStateRepository.findCurrentPageStateForGameState(gameState.getId()).orElse(null));

		return gameState;
	}

	/**
	 * Set game state Used when user continues to play game
	 *
	 * @param gameStateId game state ID
	 * @param user        authenticated user
	 */
	public void setGameState(String gameStateId, User user) {
		GameState gameState = gameStateRepository.findGameState(gameStateId).orElseThrow();
		userService.updateGameState(user, gameState);
		userService.updatePageState(user,
				pageStateRepository.findCurrentPageStateForGameState(gameState.getId()).orElse(null));
	}

	/**
	 * Update money and time in storyline
	 *
	 * @param virtualTime
	 * @param money
	 * @param gameState
	 */
	public void updateVirtualTimeAndMoney(@Nullable VirtualTime virtualTime, @Nullable Money money,
			GameState gameState) {
		Duration currentTime = gameState.getVirtualTimeBalance();
		if (virtualTime != null) {
			gameState.setVirtualTimeBalance(currentTime.plus(virtualTime.getTime()));
		}

		if (money != null) {
			double currentBalance = gameState.getMoneyBalance();
			gameState.setMoneyBalance(currentBalance + money.getAmount());
		}

		save(gameState);
	}

	/**
	 * Finish current topic
	 * 
	 * @param gameState current topic state
	 */
	public void finish(GameState gameState) {
		gameState.setFinishedAt(LocalDateTime.now());
		save(gameState);
	}

	/**
	 * Saves topic state
	 *
	 * @param gameState
	 * @return
	 */
	private GameState save(GameState gameState) {
		gameState.setLastSavedAt(LocalDateTime.now());
		return gameStateRepository.save(gameState);
	}
}
