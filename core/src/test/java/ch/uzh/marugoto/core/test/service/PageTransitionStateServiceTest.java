package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.service.ExerciseStateService;
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
    private PageStateRepository pageStateRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
    private User user;
    private Page page1;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        page1 = pageRepository.findByTitle("Page 1");
    }

    @Test
    public void testInitializeStateForNewPage() {
    	PageState pageState = user.getCurrentPageState(); 
    	pageTransitionStateService.initializeStateForNewPage(pageState);
    	assertEquals(pageTransitionRepository.findByPageId(page1.getId()).size(), pageState.getPageTransitionStates().size());
    }
    
    @Test 
    public void testUpdatePageTransitionStatesAvailabilityIfAvailabilityIsChanged() {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
    	ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);    
    	
    	exerciseState.setInputState("Thank you");
		exerciseStateRepository.save(exerciseState);
        var availabilityChanged = pageTransitionStateService.updatePageTransitionStateAvailability(user);


        assertTrue(availabilityChanged);
    }
    
    @Test 
    public void testUpdatePageTransitionStatesAvailabilityIfAvailabilityIsNotChanged() {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();
    	ExerciseState exerciseState = exerciseStateService.getExerciseState(pageTransition.getCriteria().get(0).getAffectedExercise(), pageState);    

        exerciseState.setInputState("Wrong solution");
		exerciseStateRepository.save(exerciseState);
        var availabilityChanged = pageTransitionStateService.updatePageTransitionStateAvailability(user);
        assertFalse(availabilityChanged);
    }
    
    @Test 
    public void testIsPageTransitionStateAvailable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var pageState = user.getCurrentPageState();
        var pageTransition = pageState.getPageTransitionStates().get(0).getPageTransition();

        Method method = PageTransitionStateService.class.getDeclaredMethod("isPageTransitionStateAvailable", PageTransition.class, PageState.class);
        method.setAccessible(true);

        var available = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertTrue(available);

        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.visited, page1)));
        available = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertTrue(available);

        pageTransition.setCriteria(List.of(new Criteria(PageCriteriaType.notVisited, page1)));
        available = (boolean) method.invoke(pageTransitionStateService, pageTransition, pageState);
        assertFalse(available);
    }

    @Test
    public void testUpdateOnTransition() throws PageTransitionNotAllowedException, PageTransitionNotFoundException {

        var pageState = user.getCurrentPageState();
        var pageTransitionState = pageState.getPageTransitionStates().get(0);
        pageTransitionState.setAvailable(true);
        pageStateRepository.save(pageState);
        var pageTransitionId = pageTransitionState.getPageTransition().getId();

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
        var pageTransitionState = pageState.getPageTransitionStates().get(0);
        var testPageTransitionState = method.invoke(pageTransitionStateService, pageState, pageTransitionState.getPageTransition());
        assertNotNull(testPageTransitionState);
        assertEquals(pageTransitionState, testPageTransitionState);
    }
}