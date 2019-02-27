package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.GameStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class GameStateServiceTest extends BaseCoreTest {
	
	@Autowired
	private GameStateRepository gameStateRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private ClassroomRepository classroomRepository;
	@Autowired
	private GameStateService gameStateService;
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
		user.setCurrentGameState(null);
		userRepository.save(user);
		assertNull(user.getCurrentGameState());

        gameStateService.initializeState(user, topic);
        var gameState = user.getCurrentGameState();

        assertNotNull(gameState.getStartedAt());
	}

	@Test
	public void testGetOpenGames() {
		var games = gameStateService.getOpenGames(user);
		assertEquals(1, games.size());

		gameStateService.finish(games.get(0));
		games = gameStateService.getOpenGames(user);
		assertEquals(0, games.size());
	}

	@Test
	public void testGetFinishedGames() {
		var games = gameStateService.getFinishedGames(user);
		assertEquals(0, games.size());
		gameStateService.finish(gameStateService.getOpenGames(user).get(0));

		games = gameStateService.getFinishedGames(user);
		assertEquals(1, games.size());
	}

	@Test
	public void testGetClassroomGames() {
		var games = gameStateService.getClassroomGames(user);
		assertEquals(0, games.size());

		// add game to classroom
		var classroom = classroomRepository.save(new Classroom("Class 1", null, LocalDate.now(), null));
		var openGame = gameStateService.getOpenGames(user).get(0);
		openGame.setClassroom(classroom);
		gameStateRepository.save(openGame);
		// check again
		games = gameStateService.getClassroomGames(user);
		assertEquals(1, games.size());
	}

	@Test
	public void testActivateGameState() {
		var newGameState = gameStateRepository.save(new GameState(topicRepository.findByActiveIsTrue().get(0)));

		assertNotEquals(newGameState.getId(), user.getCurrentGameState().getId());
		gameStateService.activateGameState(newGameState.getId(), user);
		assertEquals(newGameState.getId(), user.getCurrentGameState().getId());
	}
	
	@Test
	public void testUpdateMoneyAndTimeBalanceInTopicState () throws SecurityException {
		double starterAmount = 15.0;
		var pageTransition = pageTransitionRepository.findByPageId(page2.getId()).get(0);

		GameState gameState = new GameState(topic);
		gameState.setMoneyBalance(starterAmount);
		gameState.setVirtualTimeBalance(Duration.ZERO);
		gameStateRepository.save(gameState);
		
 		pageTransition.setMoney(starterAmount);
		pageTransition.setTime(new VirtualTime(Duration.ofMinutes(20), true));
		pageTransitionRepository.save(pageTransition);

		assertThat(gameState.getMoneyBalance(), is(starterAmount));
		assertThat(gameState.getVirtualTimeBalance(), is(Duration.ZERO));

		gameStateService.updateVirtualTimeAndMoney(pageTransition.getTime(), pageTransition.getMoney(), gameState);

		assertThat(gameState.getMoneyBalance(), is(starterAmount + pageTransition.getMoney().getAmount()));
		assertThat(gameState.getVirtualTimeBalance(), is(Duration.ZERO.plus(pageTransition.getTime().getTime())));
	}

	@Test
	public void testFinishGameState() {
		var game = gameStateRepository.findNotFinishedStates(user.getId()).get(0);
		assertNull(game.getFinishedAt());
		gameStateService.finish(game);
		assertNotNull(game.getFinishedAt());
	}
}
