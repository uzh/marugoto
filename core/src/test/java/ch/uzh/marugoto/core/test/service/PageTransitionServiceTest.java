package ch.uzh.marugoto.core.test.service;

import org.checkerframework.checker.units.qual.A;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageCriteriaType;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
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
    private ComponentRepository componentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;

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
    public void testIsPageTransitionAllowed() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionService.class.getDeclaredMethod("isPageTransitionAllowed", PageTransition.class, User.class);
        method.setAccessible(true);

        var page = pageRepository.findByTitle("Page 3");
        pageService.getPageState(page, user);
        var components = componentRepository.findByPageId(page.getId());
        var pageTransition = pageTransitionService.getAllPageTransitions(page.getId()).get(0);
        // true
        pageTransition.addCriteria(new Criteria(ExerciseCriteriaType.noInput, (Exercise) components.get(0)));
        var allowed = (boolean) method.invoke(pageTransitionService, pageTransition, user);
        assertTrue(allowed);
        // false
        pageTransition.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, (Exercise) components.get(0)));
        allowed = (boolean) method.invoke(pageTransitionService, pageTransition, user);
        assertFalse(allowed);
        // true
        pageTransition.addCriteria(new Criteria(PageCriteriaType.notVisited, pageTransition.getTo()));
        allowed = (boolean) method.invoke(pageTransitionService, pageTransition, user);
        assertTrue(allowed);
    }

    @Test
    public void testPageCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PageTransitionService.class.getDeclaredMethod("pageCriteriaSatisfied", List.class, Criteria.class);
        method.setAccessible(true);

        var page1 = pageRepository.findByTitle("Page 1");
        var page2 = pageRepository.findByTitle("Page 2");

        var pageStates = new ArrayList<>();
        pageStates.add(new PageState(page1, user));

        // true
        var criteria = new Criteria(PageCriteriaType.visited, page1);
        var satisfied = (boolean) method.invoke(pageTransitionService, pageStates, criteria);
        assertTrue(satisfied);
        // false
        criteria = new Criteria(PageCriteriaType.notVisited, page1);
        satisfied = (boolean) method.invoke(pageTransitionService, pageStates, criteria);
        assertFalse(satisfied);

        // true
        criteria = new Criteria(PageCriteriaType.notVisited, page2);
        satisfied = (boolean) method.invoke(pageTransitionService, pageStates, criteria);
        assertTrue(satisfied);
    }
}
