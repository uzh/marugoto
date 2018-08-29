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
	public void test1InitPageStates() {
		// Page 1
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.initPageStates(page, user);
		assertNotNull(pageState);
		assertEquals(user.getId(), pageState.getUser().getId());
		assertEquals(0, pageState.getExerciseStates().size());
		
		// Page 2
		page = pageRepository.findByTitle("Page 2");
		pageState = stateService.initPageStates(page, user);
		assertNotNull(pageState);
		assertEquals(user.getId(), pageState.getUser().getId());
		assertEquals(1, pageState.getExerciseStates().size());
	}
	
	@Test
	public void test3GetPageStateWhenNotExist() {
		var page = pageRepository.save(new Page("Page State 1", true, null));
		var user = userRepository.findByMail("unittest@marugoto.ch");
		
		var pageState = stateService.getPageState(page, user);

		assertNull(pageState);
	}

	@Test
	public void test2CreatePageState() {
		// Create
		var page = pageRepository.save(new Page("Page State 1", true, null));
		var user = userRepository.findByMail("unittest@marugoto.ch");		
		var pageState = stateService.createPageState(page, user);
		
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "unittest@marugoto.ch");
		
		// Load
		var loadedPageState = stateService.getPageState(page, user);

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getId(),pageState.getId());
		assertEquals(pageState.getUser().getMail(), "unittest@marugoto.ch");
	}
	
	@Test
	public void test4UpdatePageStateAfterTransition() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransition = pageTransitionRepository.findAll().iterator().next();
		
		var pageStateBeforeUpdate = pageStateRepository.findByPageAndUser(pageTransition.getFrom().getId(), user.getId()).get();
		var pageStateAfterUpdate = stateService.updatePageStateAfterTransition(false, pageTransition, user);
		
		assertNull(pageStateBeforeUpdate.getLeftAt());
		assertNotNull(pageStateAfterUpdate.getLeftAt());
		assertEquals(1, pageStateAfterUpdate.getPageTransitionStates().size());
		assertFalse(pageStateAfterUpdate.getPageTransitionStates().get(0).isChosenByPlayer());
		
	}
	
	@Test
	public void test5CreatePageTransitionState() {
		var pageTransition = pageTransitionRepository.findAll().iterator().next();
		var pageTransitionState = stateService.createPageTransitionState(true, false, pageTransition);
		
		assertNotNull(pageTransitionState);
		assertTrue(pageTransitionState.isAvailable());
		assertFalse(pageTransitionState.isChosenByPlayer());
	}
}
