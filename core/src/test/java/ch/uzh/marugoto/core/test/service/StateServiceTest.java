package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
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
}
