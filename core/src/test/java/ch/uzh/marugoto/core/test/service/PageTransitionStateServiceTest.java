package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class PageTransitionStateServiceTest extends BaseCoreTest {

    @Autowired
    private PageTransitionStateService pageTransitionStateService;
    @Autowired
    private PageService pageService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageTransitionRepository pageTransitionRepository;
    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void testIsAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("isStateAvailable", PageState.class, PageTransition.class);
        method.setAccessible(true);

        // true
        var pageState = user.getCurrentPageState();
        var available = (boolean) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition());
        assertTrue(available);
        // false
        pageState = pageService.getPageState(pageRepository.findByTitle("Page 2"), user);
        available = (boolean) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition());
        assertFalse(available);
    }

    @Test
    public void testUpdateStateAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("updateState", PageState.class, PageTransition.class, boolean.class);
        method.setAccessible(true);

        var pageState = user.getCurrentPageState();
        pageState = (PageState) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), true);
        assertTrue(pageState.getPageTransitionStates().get(0).isAvailable());

        pageState = (PageState) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), false);
        assertFalse(pageState.getPageTransitionStates().get(0).isAvailable());
    }

    @Test
    public void testUpdateStateChosenBy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("updateState", PageState.class, PageTransition.class, TransitionChosenOptions.class);
        method.setAccessible(true);

        var pageState = user.getCurrentPageState();
        pageState = (PageState) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(0).getPageTransition(), TransitionChosenOptions.autoTransition);
        assertEquals(TransitionChosenOptions.autoTransition, pageState.getPageTransitionStates().get(0).getChosenBy());

        pageState = (PageState) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(1).getPageTransition(), TransitionChosenOptions.player);
        assertEquals(TransitionChosenOptions.player, pageState.getPageTransitionStates().get(1).getChosenBy());

        pageState = (PageState) method.invoke(pageTransitionStateService, pageState, pageState.getPageTransitionStates().get(1).getPageTransition(), TransitionChosenOptions.none);
        assertEquals(TransitionChosenOptions.none, pageState.getPageTransitionStates().get(1).getChosenBy());
    }

    @Test
    public void testCreateStates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionStateService.class.getDeclaredMethod("createStates", PageState.class, List.class);
        method.setAccessible(true);

        var pageTransitions = pageTransitionRepository.findByPageId(user.getCurrentPageState().getPage().getId());
        var pageTransitionStates = (PageState) method.invoke(pageTransitionStateService, user.getCurrentPageState(), pageTransitions);
        assertEquals(2, pageTransitionStates.getPageTransitionStates().size());
    }
}