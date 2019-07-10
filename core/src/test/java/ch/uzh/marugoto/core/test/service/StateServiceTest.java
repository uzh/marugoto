package ch.uzh.marugoto.core.test.service;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.GameStateNotInitializedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StateServiceTest extends BaseCoreTest {

	@Autowired
	private StateService stateService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
	@Autowired
	private PageStateRepository pageStateRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private GameStateRepository topicStateRepository;
	
	private User user;
	private Page page;
	

	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
		page = pageRepository.findByTitle("Page 1");
	}

	@Test(expected = GameStateNotInitializedException.class)
	public void testGetStatesWhenTopicIsNotSelected() throws GameStateNotInitializedException {
		user.setCurrentGameState(null);
		userRepository.save(user);
        stateService.getStates(user);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetStates() throws GameStateNotInitializedException {
		user.setCurrentGameState(topicStateRepository.save(new GameState(topicRepository.findByActiveIsTrue().get(0))));
		var states = stateService.getStates(user);
		var transitionStates = (List<PageTransitionState>) states.get("pageTransitionStates");
		assertTrue(states.containsKey("pageTransitionStates"));
		assertThat(transitionStates.size(), is(2));
		assertTrue(states.containsKey("pageComponents"));
	}
	
	@Test
	public void testDoTransition() throws PageTransitionNotAllowedException {
		var pageState = user.getCurrentPageState();
		pageState.getPageTransitionStates().get(0).setAvailable(true);
		pageStateRepository.save(pageState);

		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		var pageTransition = pageTransitions.get(0);
		var nextPage = stateService.doPageTransition(TransitionChosenOptions.player, pageTransition.getId(), user);
		
		assertNotNull(nextPage);
		assertEquals(pageTransition.getTo().getId(), nextPage.getId());
	}
	
	@Test
	public void testStartTopic() {
		Topic topic = new Topic("Topic1", null, true, page);
		topicRepository.save(topic);
		stateService.startTopic(topic, user);
		assertNotNull(user.getCurrentGameState());
	}
	
	@Test
	public void testInitializeStatesForNewPage() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = StateService.class.getDeclaredMethod("initializeStatesForNewPage", Page.class, User.class, GameState.class);
        method.setAccessible(true);

		Page page = pageRepository.findByTitle("Page 2");
		method.invoke(stateService, page, user, user.getCurrentGameState());
		assertNotNull(user.getCurrentPageState());
		assertNotNull(exerciseStateRepository.findByPageStateId(user.getCurrentPageState().getId()));
		assertFalse(user.getCurrentPageState().getPageTransitionStates().isEmpty());
		assertNotNull(notebookService.getNotebookEntry(user.getCurrentPageState().getPage()));
	}
}
