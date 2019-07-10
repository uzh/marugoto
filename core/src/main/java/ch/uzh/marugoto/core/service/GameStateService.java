package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
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
	
	public List<GameState> getByTopicAndUser(String userId, String topicId) {
		return gameStateRepository.findByTopicAndUser(userId, topicId);
	}
	
	/**
	 * Return classroom game state
	 *
	 * @param classroomId
	 * @param userId
	 * @return gameState
	 */
	public GameState getClassroomGameState(String classroomId, String userId) {
		return gameStateRepository.findByClassroomAndUser(classroomId, userId).orElse(null);
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
		userService.updatePageState(user, pageStateRepository.findCurrentPageStateForGameState(gameState.getId()).orElse(null));

		return gameState;
	}

	/**
	 * Set game state Used when user continues to play game
	 *
	 * @param gameStateId game state ID
	 * @param user        authenticated user
	 */
	public void setGameState(String gameStateId, User user) {
		GameState gameState = findById(gameStateId);
		userService.updateGameState(user, gameState);
		userService.updatePageState(user, pageStateRepository.findCurrentPageStateForGameState(gameState.getId()).orElse(null));
	}

	/**
	 * Find game state by ID
	 *
	 * @param gameStateId
	 * @return
	 */
	public GameState findById(String gameStateId) {
		return gameStateRepository.findGameState(gameStateId).orElseThrow();
	}

	/**
	 * Update money and time
	 *
	 * @param virtualTime
	 * @param money
	 * @param gameState
	 */
	public void updateVirtualTimeAndMoney(PageTransition pageTransition, GameState gameState) {
		if (pageTransition.getTime() != null) {
			Duration currentTime = gameState.getVirtualTimeBalance();
			gameState.setVirtualTimeBalance(currentTime.plus(pageTransition.getTime().getTime()));
		}
		if (pageTransition.getMoney() != null) {
			double currentBalance = gameState.getMoneyBalance();
			gameState.setMoneyBalance(currentBalance + pageTransition.getMoney().getAmount());
		}
		save(gameState);
	}
	
	/**
	 * Initially set money and time for page
	 *
	 * @param virtualTime
	 * @param money
	 * @param gameState
	 */
	public void initializeVirtualTimeAndMoney(Page nextPage, GameState gameState) {
		if (nextPage.getTime() != null) {
			gameState.setVirtualTimeBalance(nextPage.getTime().getTime());
		}
		if (nextPage.getMoney() != null) {
			gameState.setMoneyBalance(nextPage.getMoney().getAmount());
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
