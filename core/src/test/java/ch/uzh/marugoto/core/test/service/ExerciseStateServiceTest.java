package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextSolution;
import ch.uzh.marugoto.core.data.entity.topic.TextSolutionMode;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.DateNotValidException;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExerciseStateServiceTest extends BaseCoreTest{
    
    @Autowired
    private PageStateService pageStateService;
	@Autowired
    private ExerciseService exerciseService;
	@Autowired
    private ExerciseStateService exerciseStateService;
	@Autowired
    private ComponentService componentService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private UserRepository userRepository;

    private PageState pageState1;
    private PageState pageState2;

    public synchronized void before() {
        super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var page1 = pageRepository.findByTitle("Page 1");
        var page2 = pageRepository.findByTitle("Page 2");

        pageState1 = pageStateService.initializeStateForNewPage(page1, user);
        pageState2 = pageStateService.initializeStateForNewPage(page2, user);
        exerciseStateService.initializeStateForNewPage(pageState1);
        exerciseStateService.initializeStateForNewPage(pageState2);
    }
    
    @Test
    public void testGetExerciseState () {
		var exercise = exerciseService.getExercises(pageState1.getPage()).get(0);

    	ExerciseState newExerciseState = new ExerciseState(exercise, "text", pageState1);
    	exerciseStateRepository.save(newExerciseState);
    	var exerciseState= exerciseStateService.getExerciseState(exercise, pageState1);
    	assertEquals (exerciseState.getPageState().getId(), pageState1.getId());
    	assertEquals (exerciseState.getExercise().getPage(), pageState1.getPage());
    }
    
    @Test
    public void testGetAllExerciseStates () {
    	var exerciseStates = exerciseStateRepository.findByPageStateId(pageState1.getId());
    	assertThat (exerciseStates.size(), is(1));
		assertThat(exerciseStates.get(0).getExercise(), instanceOf(TextExercise.class));
    }
    
    @Test
    public void testInitializeStateForNewPage() {
    	var exerciseStates = exerciseStateRepository.findByPageStateId(pageState2.getId());
		assertFalse(exerciseStates.isEmpty());
		assertThat (exerciseStates.get(0).getExercise(), instanceOf(RadioButtonExercise.class));
    }

    @Test
    public void testUpdateExerciseState() throws Exception {
    	String inputState = "updatedState";
        var exerciseStates = exerciseStateRepository.findByPageStateId(pageState1.getId());
        ExerciseState updatedExerciseState = exerciseStateService.updateExerciseState(exerciseStates.get(0).getId(), inputState);
    	assertThat(updatedExerciseState.getInputState(), is(inputState));
    }

    @Test
    public void testAddComponentResourceState() {
        var componentResources = componentService.getComponentResources(pageState1.getPage());
        boolean hasState = componentResources.stream().anyMatch(componentResource -> componentResource.getState() != null);
        assertFalse(hasState);

        exerciseStateService.getComponentResources(userRepository.findByMail("unittest@marugoto.ch").getCurrentPageState());
        hasState = componentResources.stream().anyMatch(componentResource -> componentResource.getState() == null);
        assertTrue(hasState);
    }

    @Test
    public void testValidateInput() throws NoSuchMethodException, IllegalAccessException {
        Method method = ExerciseStateService.class.getDeclaredMethod("validateInput", ExerciseState.class, String.class);
        method.setAccessible(true);

        var exerciseState = new ExerciseState(new DateExercise());
        try {
            method.invoke(exerciseStateService, exerciseState, "21-3.2123");
            assertEquals("dd/mm/yyyy", method.invoke(exerciseStateService, exerciseState, "21/3/2123"));
        } catch (InvocationTargetException e) {
            assertThat(e.getCause(), instanceOf(DateNotValidException.class));
        }
    }
    
    @Test
    public void testExerciseCriteriaIsSatisfied() {
        TextExercise textExercise = new TextExercise();
        textExercise.addTextSolution(new TextSolution("yes", TextSolutionMode.fullmatch));
        ExerciseState exerciseState = new ExerciseState(textExercise, "yes");
        // true
        var satisfied = exerciseStateService.exerciseSolved(exerciseState, ExerciseCriteriaType.correctInput);
        assertTrue(satisfied);
        // false
        satisfied = exerciseStateService.exerciseSolved(exerciseState, ExerciseCriteriaType.incorrectInput);
        assertFalse(satisfied);
        // true
        exerciseState.setInputState("");
        satisfied = exerciseStateService.exerciseSolved(exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
        // true
        exerciseState.setInputState(null);
        satisfied = exerciseStateService.exerciseSolved(exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
    }
}
