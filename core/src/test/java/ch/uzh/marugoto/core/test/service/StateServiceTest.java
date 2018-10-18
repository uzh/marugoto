package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the StateService class
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StateServiceTest extends BaseCoreTest {

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StateService stateService;

	@Autowired
	private StorylineStateRepository storylineStateRepository;
	
	@Test
	public void test2GetPageState() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var loadedPageState = stateService.getPageState(page, user);

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getPage().getId(), page.getId());
	}

	@Test
	public void test1IsPageStateCreatedWhenItIsMissing() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page.getTitle());
	}

	@Test
	public void test5UpdateStatesAfterTransition() {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		
		var pageState = stateService.getPageState(page, user);
		assertNull(pageState.getLeftAt());
		// TODO
//		assertFalse(pageState.getPageTransitionStates().get(0).getChosenBy());

		stateService.updateStatesAfterTransition(true, pageTransitions.get(0), user);
		pageState = stateService.getPageState(page, user);

		assertNotNull(pageState.getLeftAt());
		// TODO
//		assertTrue(pageState.getPageTransitionStates().get(0).getChosenBy());
	}

	@Test
	public void test6UpdateExerciseState() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(pageRepository.findByTitle("Page 1"), user);
		var exerciseState = stateService.getExercisesState(pageState).get(0);
		var inputText = "This is some dummy input from user";
		var updatedExerciseState = stateService.updateExerciseState(exerciseState.getId(), inputText);
		
		assertEquals(updatedExerciseState.getInputState(), inputText);
		assertNotSame(updatedExerciseState.getInputState(), exerciseState.getInputState());
	}
	
	@Test
	public void test7IsStorylineStateCreatedWhenItIsMissing() {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user);

		assertNotNull(pageState.getStorylineState());
		assertEquals(pageState.getStorylineState().getId(), user.getCurrentPageState().getStorylineState().getId());
	}
	
	@Test
	public void test8StorylineStateNotCreatedIfPageIsNotEntryPoint() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user);

		assertNull(pageState.getStorylineState());
	}

	@Test
	public void test9GetAllStates() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");

		HashMap<String, Object> states = stateService.getAllStates(page, user);

		assertTrue(states.containsKey("pageState"));
	 	assertTrue(states.containsKey("exerciseState"));
	}
	
	@Test
	public void testUpdateMoneyAndTimeBalanceInStorylineState () throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		
		Method method = StateService.class.getDeclaredMethod("updateMoneyAndTimeBalanceInStorylineState", Money.class, VirtualTime.class,StorylineState.class);
		method.setAccessible(true);
		double starterAmount = 15.0;	
		var page = pageRepository.findByTitle("Page 2");
		var pageTransition = pageTransitionRepository.findByPageId(page.getId()).get(0);
		
		StorylineState storylineState = new StorylineState(page.getStoryline());
		storylineState.setMoneyBalance(starterAmount);
		storylineState.setVirtualTimeBalance(Duration.ZERO);
		storylineStateRepository.save(storylineState);

		pageTransition.setMoney(new Money(starterAmount));
		pageTransition.setVirtualTime(new VirtualTime(Duration.ofMinutes(20), true));
		pageTransitionRepository.save(pageTransition);
		
		method.invoke(stateService, pageTransition.getMoney(), pageTransition.getVirtualTime(),storylineState);
		
		assertThat(storylineState.getMoneyBalance(), is(starterAmount + pageTransition.getMoney().getAmount()));
		assertThat(storylineState.getVirtualTimeBalance(), is(Duration.ZERO.plus(pageTransition.getVirtualTime().getTime())));
		
	}
	
}
