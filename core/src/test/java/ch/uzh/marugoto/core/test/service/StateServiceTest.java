package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
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
	private PageStateRepository pageStateRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StateService stateService;

	@Test
	public void test1IsPageStateCreatedWhenItIsMissing() {
		// Create
		var page = pageRepository.save(new Page("Page State 1", true, null,null));
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "unittest@marugoto.ch");
	}

	@Test
	public void test2GetPageState() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var loadedPageState = stateService.getPageState(page, user);

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getPage().getId(), page.getId());
		assertEquals(loadedPageState.getUser().getMail(), "unittest@marugoto.ch");
	}

	@Test
	public void test3GetPageTransitionState() {
		var pageId = pageRepository.findByTitle("Page 2").getId();
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(pageId);
		var pageTransitionState = stateService.getPageTransitionState(pageTransitions.get(0), user);

		assertNotNull(pageTransitionState);
		assertEquals(pageTransitions.get(0).getId(), pageTransitionState.getPageTransition().getId());
		assertEquals(pageTransitions.get(0).getFrom().getId(), pageTransitionState.getPageTransition().getFrom().getId());
	}

	@Test
	public void test4GetPageTransitionStates() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransitionStates = stateService.getPageTransitionStates(page, user);
		var pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(page.getId());
		
		assertNotNull(pageTransitionStates);
		assertEquals(pageTransitionStates.size(), pageTransitions.size());

	}

	@Test
	public void test2UpdateStatesAfterTransition() {
		var pageId = pageRepository.findByTitle("Page 2").getId();
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(pageId);

		var pageStateBeforeUpdate = pageStateRepository.findByPageAndUser(pageTransitions.get(0).getFrom().getId(),
				user.getId());
		stateService.updateStatesAfterTransition(false, pageTransitions.get(0), user);
		var pageStateAfterUpdate = pageStateRepository.findByPageAndUser(pageTransitions.get(0).getFrom().getId(),
				user.getId());

		assertNull(pageStateBeforeUpdate.getLeftAt());
		assertNotNull(pageStateAfterUpdate.getLeftAt());
		assertNotNull(stateService.getPageTransitionState(pageTransitions.get(0), user));
		assertFalse(stateService.getPageTransitionState(pageTransitions.get(0), user).isChosenByPlayer());
	}

	@Test
	public void test3UpdateExerciseState() {
		var pageState = pageStateRepository.findByPageAndUser(pageRepository.findByTitle("Page 2").getId(),
				userRepository.findByMail("unittest@marugoto.ch").getId());
		var exerciseState = stateService.getExerciseStates(pageState).get(0);
		var inputText = "This is some dummy input from user";
		var updatedExerciseState = stateService.updateExerciseState(exerciseState.getId(), inputText);
		
		assertTrue(updatedExerciseState.getInputState() != exerciseState.getInputState());
		assertEquals(updatedExerciseState.getInputState(), inputText);
	}
}
