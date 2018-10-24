package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PageTransitionServiceTest extends BaseCoreTest {

    @Autowired
    private PageRepository pageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PageTransitionService pageTransitionService;
    
    @Autowired
    private PageService pageService;

    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void isTransitionAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionService.class.getDeclaredMethod("isTransitionAvailable", PageTransition.class, User.class);
        method.setAccessible(true);

        // true
        var page = pageRepository.findByTitle("Page 1");
        var pageTransition = pageTransitionService.getAllPageTransitions(page).get(0);
        var available = (boolean) method.invoke(pageTransitionService, pageTransition, user);
        assertTrue(available);

        // false
        page = pageRepository.findByTitle("Page 3");
        pageService.getPageState(page, user);
        pageTransition = pageTransitionService.getAllPageTransitions(page).get(0);
        available = (boolean) method.invoke(pageTransitionService, pageTransition, user);
        assertFalse(available);
    }

//    @Test
//    public void testPageCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Method method = PageTransitionService.class.getDeclaredMethod("isCriteriaSatisfied", PageTransition.class, List.class);
//        method.setAccessible(true);
//
//        var page1 = pageRepository.findByTitle("Page 1");
//        var page2 = pageRepository.findByTitle("Page 2");
//        var transition = pageTransitionService.getAllPageTransitions(page1).get(0);
//
//        var pageStates = new ArrayList<>();
//        pageStates.add(new PageState(page1, user));
//
//        // true
//        transition.addCriteria(new Criteria(PageCriteriaType.visited, page1));
//        var satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//        assertTrue(satisfied);
//        // false
//        transition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page1)));
//        satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//        assertFalse(satisfied);
//        // true
//        transition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page2)));
//        satisfied = (boolean) method.invoke(pageTransitionService, transition, pageStates);
//        assertTrue(satisfied);
//    }
}
