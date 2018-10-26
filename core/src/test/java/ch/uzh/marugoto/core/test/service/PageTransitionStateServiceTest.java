package ch.uzh.marugoto.core.test.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;


public class PageTransitionStateServiceTest extends BaseCoreTest {

    @Autowired
    private PageTransitionStateService pageTransitionStateService;

    @Autowired
    private PageStateService pageStateService;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageTransitionService pageTransitionService;
    
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
    
    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void testIsStateAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("isStateAvailable", PageState.class, PageTransition.class);
        method.setAccessible(true);

        // true
        var pageState = user.getCurrentPageState();
        var available = (boolean) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition());
        assertTrue(available);
        // false
        pageState = pageStateService.getState(pageRepository.findByTitle("Page 2"), user);
        available = (boolean) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition());
        assertFalse(available);
    }

    @Test
    public void testCreateStates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("createStates", PageState.class);
        method.setAccessible(true);

        method.invoke(pageTransitionStateService, user.getCurrentPageState());
        assertEquals(2, user.getCurrentPageState().getPageTransitionStates().size());
    }

    @Test
    public void testUpdateStateAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("updateState", PageState.class, PageTransition.class, boolean.class);
        method.setAccessible(true);

        var pageState = user.getCurrentPageState();
        method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), true);
        assertTrue(pageState.getPageTransitionStates().get(0).isAvailable());

        method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), false);
        assertFalse(pageState.getPageTransitionStates().get(0).isAvailable());
    }

    @Test
    public void testUpdateStateChosenBy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("updateState", PageState.class, PageTransition.class, TransitionChosenOptions.class);
        method.setAccessible(true);

        var pageState = user.getCurrentPageState();
        method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), TransitionChosenOptions.autoTransition);
        assertEquals(TransitionChosenOptions.autoTransition, pageState.getPageTransitionStates().get(0).getChosenBy());

        method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(1).getPageTransition(), TransitionChosenOptions.player);
        assertEquals(TransitionChosenOptions.player, pageState.getPageTransitionStates().get(1).getChosenBy());

        method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(1).getPageTransition(), TransitionChosenOptions.none);
        assertEquals(TransitionChosenOptions.none, pageState.getPageTransitionStates().get(1).getChosenBy());
    }
    
	@Test
	public void testDoTransition() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 1");
		var pageState = pageStateService.getState(page, user);
		pageState.getPageTransitionStates().get(0).setAvailable(true);
		pageStateRepository.save(pageState);


		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		var pageTransition = pageTransitions.get(0);
		var nextPage = pageTransitionStateService.doTransition(true, pageTransition.getId(), user);
		
		assertNotNull(nextPage);
		assertEquals(pageTransition.getTo().getId(), nextPage.getId());
	}

	@Test(expected = PageTransitionNotAllowedException.class)
	public void testDoTransitionWhenIsNotAllowed() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 2");
		// init page state
		pageStateService.getState(page, user);
		var transitions = pageTransitionRepository.findByPageId(page.getId());

		pageTransitionStateService.doTransition(false, transitions.get(0).getId(), user);
	}
    
    @Test
    public void isTransitionAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("isTransitionAvailable", PageTransition.class, User.class);
        method.setAccessible(true);

        // true
        var page = pageRepository.findByTitle("Page 1");
        var pageTransition = pageTransitionService.getAllPageTransitions(page).get(0);
        var available = (boolean) method.invoke(pageTransitionStateService, pageTransition, user);
        assertTrue(available);

        // false
        page = pageRepository.findByTitle("Page 3");
        pageStateService.getState(page, user);
        pageTransition = pageTransitionService.getAllPageTransitions(page).get(0);
        available = (boolean) method.invoke(pageTransitionStateService, pageTransition, user);
        assertFalse(available);
    }
    
//  @Test
//  public void testPageCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//      Method method = PageTransitionService.class.getDeclaredMethod("isCriteriaSatisfied", PageTransition.class, List.class);
//      method.setAccessible(true);
//
//      var page1 = pageRepository.findByTitle("Page 1");
//      var page2 = pageRepository.findByTitle("Page 2");
//      var transition = pageTransitionService.getAllPageTransitions(page1).get(0);
//
//      var pageStates = new ArrayList<>();
//      pageStates.add(new PageState(page1, user));
//
//      // true
//      transition.addCriteria(new Criteria(PageCriteriaType.visited, page1));
//      var satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//      assertTrue(satisfied);
//      // false
//      transition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page1)));
//      satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//      assertFalse(satisfied);
//      // true
//      transition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page2)));
//      satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//      assertTrue(satisfied);
//  }

}