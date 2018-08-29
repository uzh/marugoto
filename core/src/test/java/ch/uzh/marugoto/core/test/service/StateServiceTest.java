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
		var page = pageRepository.save(new Page("Page State 1", true, null));
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = stateService.getPageState(page, user);
		
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "unittest@marugoto.ch");
		
		
	}
	
	@Test
	public void test2GetPageState() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");		
		var loadedPageState = stateService.getPageState(page, user);

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getPage().getId(), page.getId());
		assertEquals(loadedPageState.getUser().getMail(), "unittest@marugoto.ch");
	}
	
	@Test
	public void test2UpdatePageStateAfterTransition() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransition = pageTransitionRepository.findAll().iterator().next();
		
		var pageStateBeforeUpdate = pageStateRepository.findByPageAndUser(pageTransition.getFrom().getId(), user.getId()).get();
		var pageStateAfterUpdate = stateService.updatePageStateAfterTransition(false, pageTransition, user);
		
		assertNull(pageStateBeforeUpdate.getLeftAt());
		assertNotNull(pageStateAfterUpdate.getLeftAt());
		assertNotNull(stateService.getPageTransitionState(pageTransition, user));
		assertFalse(stateService.getPageTransitionState(pageTransition, user).isChosenByPlayer());
		
	}
	
	@Test
	public void test3CreatePageTransitionState() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageTransition = pageTransitionRepository.findAll().iterator().next();
		var pageTransitionState = stateService.createPageTransitionState(true, pageTransition, user);

		assertNotNull(pageTransitionState);
		assertTrue(pageTransitionState.isAvailable());
		assertFalse(pageTransitionState.isChosenByPlayer());
	}
}
