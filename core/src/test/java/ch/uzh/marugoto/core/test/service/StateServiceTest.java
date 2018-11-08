package ch.uzh.marugoto.core.test.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StateServiceTest extends BaseCoreTest {

	@Autowired
	private StateService stateService;
	@Autowired
	private PageService pageService;
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
	
	@Test
	public void testGetStates() {
        User user = userRepository.findByMail("unittest@marugoto.ch");
        var states = stateService.getStates(user);
        assertTrue(states.containsKey("exerciseStates"));
        assertTrue(states.containsKey("notebookEntries"));
	}
	
	@Test
	public void testDoTransition() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 1");
		User user = userRepository.findByMail("unittest@marugoto.ch");
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
	public void testStartModule() {
		User authenticatedUser = userRepository.findByMail("unittest@marugoto.ch");
		Page page = pageService.getTopicStartPage();
		stateService.startModule(authenticatedUser);
		var pageState = pageStateRepository.findByPageIdAndUserId(page.getId(), authenticatedUser.getId());
		assertNotNull(pageState);
	}
	
	@Test
	public void testInitializeStatesForNewPage() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = StateService.class.getDeclaredMethod("initializeStatesForNewPage", Page.class, User.class);
        method.setAccessible(true);

		Page page = pageRepository.findByTitle("Page 2");
		User user = userRepository.findByMail("unittest@marugoto.ch");
		PageState pageState = (PageState) method.invoke(stateService, page, user);
		assertNotNull(pageState);
		assertNotNull(exerciseStateRepository.findByPageStateId(pageState.getId()));
		assertFalse(pageState.getPageTransitionStates().isEmpty());
		assertNotNull(notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryCreateAt.enter));
	}
	
	
	
}
