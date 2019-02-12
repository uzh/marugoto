package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.TopicState;
import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.TopicStateRepository;

@Service
public class TopicStateService {

	@Autowired
	private TopicStateRepository topicStateRepository;
	@Autowired
	private UserService userService;

	/**
	 * Initialize or finish topic state if one topic is started
	 *
	 * @param user
	 * @return void
	 */
	public TopicState initializeState(User user, Topic topic) {
		TopicState topicState = new TopicState(topic);

		if (topicState.equals(user.getCurrentTopicState())) {
			topicState = user.getCurrentTopicState();
		} else {
			userService.updateTopicState(user, topicState);
		}

		updateVirtualTimeAndMoney(null, null, topicState);
		return topicState;
	}

	/**
	 * Update money and time in storyline
	 *
	 * @param virtualTime
	 * @param money
	 * @param topicState
	 */
	public void updateVirtualTimeAndMoney(@Nullable VirtualTime virtualTime, Money money, @Nullable TopicState topicState) {
		if (topicState != null) {
			Duration currentTime = topicState.getVirtualTimeBalance();
			if (virtualTime != null) {
				topicState.setVirtualTimeBalance(currentTime.plus(virtualTime.getTime()));
			}

			if (money != null) {
				double currentBalance = topicState.getMoneyBalance();
				topicState.setMoneyBalance(currentBalance + money.getAmount());
			}

			save(topicState);
		}
	}

	/**
	 * Saves topic state
	 *
	 * @param topicState
	 * @return
	 */
	private TopicState save(TopicState topicState) {
		topicState.setLastSavedAt(LocalDateTime.now());
		return topicStateRepository.save(topicState);
	}
}
