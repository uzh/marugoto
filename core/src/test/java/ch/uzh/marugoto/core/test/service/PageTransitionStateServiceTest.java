package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageCriteriaType;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


public class PageTransitionStateServiceTest extends BaseCoreTest {

    @Autowired
    private PageTransitionStateService pageTransitionStateService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageTransitionService pageTransitionService;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void testInitializeStateForNewPage() {
    	var page = pageRepository.findByTitle("Page 1");
    	PageState pageState = user.getCurrentPageState(); 
    	pageTransitionStateService.initializeStateForNewPage (pageState);
    	assertEquals(pageTransitionRepository.findByPageId(page.getId()).size(), pageState.getPageTransitionStates().size());
    }
    
    @Test 
    public void testUpdatePageTransitionStatesAvailabilityIfAvailabilityIsChanged() {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
    	ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);    
    	
    	exerciseState.setInputState("input");
		exerciseStateRepository.save(exerciseState);
        var availabilityChanged = pageTransitionStateService.updatePageTransitionStatesAvailability(user);
        assertTrue(availabilityChanged);
    }
    
    @Test 
    public void testUpdatePageTransitionStatesAvailabilityIfAvailabilityIsNotChanged() {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
    	ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);    

        exerciseState.setInputState("Thank");
		exerciseStateRepository.save(exerciseState);
        var availabilityChanged = pageTransitionStateService.updatePageTransitionStatesAvailability(user);
        assertFalse(availabilityChanged);
    }
    
    @Test 
    public void testIsPageTransitionStateAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    	var page = pageRepository.findByTitle("Page 1");
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();

        Method method = PageTransitionStateService.class.getDeclaredMethod("isPageTransitionStateAvailable", PageTransition.class, PageState.class);
        method.setAccessible(true);

        var available = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertFalse(available);

        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.visited, page)));
        available = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertTrue(available);
    }

    @Test
    public void testUpdateOnTransition() throws PageTransitionNotAllowedException, PageTransitionNotFoundException {

        var pageState = user.getCurrentPageState();
        var pageTransitionId = pageState.getPageTransitionStates().get(0).getPageTransition().getId();
        pageTransitionStateService.updateOnTransition(TransitionChosenOptions.autoTransition, pageTransitionId, user);
        assertEquals(TransitionChosenOptions.autoTransition, pageState.getPageTransitionStates().get(0).getChosenBy());

        pageTransitionStateService.updateOnTransition(TransitionChosenOptions.none, pageTransitionId, user);
        assertNotEquals(TransitionChosenOptions.autoTransition, pageState.getPageTransitionStates().get(0).getChosenBy());
    }
    
    @Test
    public void testGetPageTransitionState() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method method = PageTransitionStateService.class.getDeclaredMethod("getPageTransitionState", PageState.class, PageTransition.class);
		method.setAccessible(true);
    	
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
        var pageTransitionState = method.invoke(pageTransitionStateService,pageState, pageTransition);
        assertNotNull(pageTransitionState);
    }
    
    @Test
    public void testIsExerciseCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    	var pageState = user.getCurrentPageState();
    	var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
    	ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);
		exerciseState.setInputState("Thank");
		exerciseStateRepository.save(exerciseState);

		Method method = PageTransitionStateService.class.getDeclaredMethod("isExerciseCriteriaSatisfied", PageTransition.class, PageState.class);
		method.setAccessible(true);

    	boolean satisfied = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertTrue(satisfied);
    }
    
    @Test
    public void testPageCriteriaSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        var page1 = pageRepository.findByTitle("Page 1");
        var page2 = pageRepository.findByTitle("Page 2");
        var pageTransition = pageTransitionService.getAllPageTransitions(page1).get(0);
        List<PageState> pageStates = new ArrayList<>();
        pageStates.add(new PageState(page1, user));

        Method method = PageTransitionStateService.class.getDeclaredMethod("isPageCriteriaSatisfied", PageTransition.class, List.class);
        method.setAccessible(true);

        // true
        pageTransition.addCriteria(new Criteria(PageCriteriaType.visited, page1));
        var satisfied = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageStates);
        assertTrue(satisfied);
        // false
        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page1)));
        satisfied = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageStates);
        assertFalse(satisfied);
        // true
        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page2)));
        satisfied = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageStates);
        assertTrue(satisfied);
   }
}