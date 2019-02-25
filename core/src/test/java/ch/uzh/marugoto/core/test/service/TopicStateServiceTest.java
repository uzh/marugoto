package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.TopicState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.TopicStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.TopicStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class TopicStateServiceTest extends BaseCoreTest {
	
	@Autowired
	private TopicStateRepository topicStateRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private TopicStateService topicStateService;
	private Topic topic;
	private User user;
	private Page page2;

	public synchronized void before() {
		super.before();
		topic = topicRepository.findAll().iterator().next();
		user = userRepository.findByMail("unittest@marugoto.ch");
		page2 = pageRepository.findByTitle("Page 2");
	}

	@Test
	public void testInitializeState() {
		user.setCurrentTopicState(null);
		userRepository.save(user);
		assertNull(user.getCurrentTopicState());

        topicStateService.initializeState(user, topic);
        var topicState = user.getCurrentTopicState();

        assertNotNull(topicState.getStartedAt());
	}
	
	@Test
	public void testUpdateMoneyAndTimeBalanceInTopicState () throws SecurityException {
		double starterAmount = 15.0;
		var pageTransition = pageTransitionRepository.findByPageId(page2.getId()).get(0);

		TopicState topicState = new TopicState(topic);
		topicState.setMoneyBalance(starterAmount);
		topicState.setVirtualTimeBalance(Duration.ZERO);
		topicStateRepository.save(topicState);
		
 		pageTransition.setMoney(starterAmount);
		pageTransition.setTime(new VirtualTime(Duration.ofMinutes(20), true));
		pageTransitionRepository.save(pageTransition);

		assertThat(topicState.getMoneyBalance(), is(starterAmount));
		assertThat(topicState.getVirtualTimeBalance(), is(Duration.ZERO));

		topicStateService.updateVirtualTimeAndMoney(pageTransition.getTime(), pageTransition.getMoney(), topicState);

		assertThat(topicState.getMoneyBalance(), is(starterAmount + pageTransition.getMoney().getAmount()));
		assertThat(topicState.getVirtualTimeBalance(), is(Duration.ZERO.plus(pageTransition.getTime().getTime())));
	}
}
