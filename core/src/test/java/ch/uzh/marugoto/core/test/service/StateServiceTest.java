package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

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
	public void test1IsPageStateCreatedWhenItIsMissing() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user.getCurrentlyPlaying());

		assertNotNull(pageState);
		assertEquals(pageState.getPartOf().getId(), user.getCurrentlyPlaying().getId());
	}

	@Test
	public void test2GetPageState() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var loadedPageState = stateService.getPageState(page, user.getCurrentlyPlaying());

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getPage().getId(), page.getId());
		assertEquals(loadedPageState.getPartOf().getId(), user.getCurrentlyPlaying().getId());
	}

	@Test
	public void test5UpdateStatesAfterTransition() {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransitions = pageTransitionRepository.findByPageId(page.getId());

		var pageStateBeforeUpdate = stateService.getPageState(page, user.getCurrentlyPlaying());
		stateService.updateStatesAfterTransition(false, pageTransitions.get(0), user);

		assertNull(pageStateBeforeUpdate.getLeftAt());
		assertNotNull(pageStateBeforeUpdate.getPageTransitionState(pageTransitions.get(0)));
		assertFalse(pageStateBeforeUpdate.getPageTransitionState(pageTransitions.get(0)).isChosenByPlayer());
	}

	@Test
	public void test6UpdateExerciseState() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = user.getCurrentlyPlaying().getCurrentlyAt();
		var exerciseState = stateService.getExerciseStates(pageState).get(0);
		var inputText = "This is some dummy input from user";
		var updatedExerciseState = stateService.updateExerciseState(exerciseState.getId(), inputText);
		
		assertEquals(updatedExerciseState.getInputState(), inputText);
		assertNotSame(updatedExerciseState.getInputState(), exerciseState.getInputState());
	}
	
	@Test
	public void test7IsStorylineStateCreatedWhenItIsMissing() {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var storylineState = stateService.getStorylineState(user, page);

		assertNotNull(storylineState);
		assertEquals(page.getTitle(), storylineState.getCurrentlyAt().getPage().getTitle());
	}
	
	@Test
	public void test8StorylineStateNotCreatedIfPageIsNotEntryPoint() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var storylineState = stateService.getStorylineState(user, page);
		assertNull(storylineState);
	}
}
