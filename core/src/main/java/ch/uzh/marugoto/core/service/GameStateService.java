package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;

@Service
public class GameStateService {

	@Autowired
	private GameStateRepository topicStateRepository;
	@Autowired
	private UserService userService;

	/**
	 * Initialize or finish topic state if one topic is started
	 *
	 * @param user
	 * @return void
	 */
	public GameState initializeState(User user, Topic topic) {
		GameState gameState = new GameState(topic);
		gameState.setUser(user);
		save(gameState);

		userService.updateGameState(user, gameState);
		return gameState;
	}

	/**
	 * Update money and time in storyline
	 *
	 * @param virtualTime
	 * @param money
	 * @param gameState
	 */
	public void updateVirtualTimeAndMoney(@Nullable VirtualTime virtualTime, @Nullable Money money, GameState gameState) {
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
		return topicStateRepository.save(gameState);
	}

}
