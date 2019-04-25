package ch.uzh.marugoto.core.test.service;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.CriteriaService;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class CriteriaServiceTest extends BaseCoreTest {
    @Autowired
    private CriteriaService criteriaService;
    @Autowired
    private ExerciseStateService exerciseStateService;
    @Autowired
    private PageTransitionService pageTransitionService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    private User user;
    private Page page1;
    private Page page2;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        page1 = pageRepository.findByTitle("Page 1");
        page2 = pageRepository.findByTitle("Page 2");
    }

    @Test
    public void testIsExerciseCriteriaSatisfied() {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
        ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);
        exerciseState.setInputState("Thank you");
        exerciseStateRepository.save(exerciseState);

        // true
        boolean satisfied = criteriaService.isExerciseCriteriaSatisfied(pageTransition, pageState);
        assertTrue(satisfied);

        // false
        var criteria = new Criteria(ExerciseCriteriaType.incorrectInput, exerciseState.getExercise());
        pageTransition.getCriteria().add(criteria);
        satisfied = criteriaService.isExerciseCriteriaSatisfied(pageTransition, pageState);
        assertFalse(satisfied);
    }

    @Test
    public void testPageCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var pageTransition = pageTransitionService.getAllPageTransitions(page1).get(0);
        List<PageState> pageStates = new ArrayList<>();
        pageStates.add(new PageState(page1));

        Method method = CriteriaService.class.getDeclaredMethod("isPageCriteriaSatisfied", PageTransition.class, List.class);
        method.setAccessible(true);

        // true
        pageTransition.addCriteria(new Criteria(PageCriteriaType.visited, page1));
        var satisfied = (boolean) method.invoke(criteriaService, pageTransition, pageStates);
        assertTrue(satisfied);
        // false
        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page1)));
        satisfied = (boolean) method.invoke(criteriaService, pageTransition, pageStates);
        assertFalse(satisfied);
        // true
        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page2)));
        satisfied = (boolean) method.invoke(criteriaService, pageTransition, pageStates);
        assertTrue(satisfied);
    }
}
