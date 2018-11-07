package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ExerciseStateServiceTest extends BaseCoreTest{
    
	@Autowired
    private ExerciseService exerciseService;
    @Autowired
    private PageRepository pageRepository;
	@Autowired
    private ExerciseStateService exerciseStateService;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired 
    private PageStateService pageStateService;
    
    @Test
    public void testGetExerciseState () {
        Page page = pageRepository.findByTitle("Page 1");
        User user = userRepository.findByMail("unittest@marugoto.ch");
		var exercise= exerciseService.getExercises(page).get(0);
    	
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
    	ExerciseState newExerciseState = new ExerciseState(exercise, "text", pageState);
    	exerciseStateRepository.save(newExerciseState);
    	
    	var exerciseState= exerciseStateService.getExerciseState(exercise, pageState);
    	assertEquals (exerciseState.getPageState().getId(), pageState.getId());
    	assertEquals (exerciseState.getExercise().getPage(), page);
    }
    
    @Test
    public void testAllGetExerciseStates () {
        Page page = pageRepository.findByTitle("Page 1");
        User user = userRepository.findByMail("unittest@marugoto.ch");
    	PageState pageState = pageStateService.initializeStateForNewPage(page, user);
    	exerciseStateService.initializeStateForNewPage(pageState);
    	
    	var exerciseStates = exerciseStateService.getAllExerciseStates(pageState);
    	assertThat (exerciseStates.size(), is(1));
		assertThat(exerciseStates.get(0).getExercise(), instanceOf(TextExercise.class));
    }
    
    @Test
    public void testInitializeStateForNewPage() throws Exception {
        Page page = pageRepository.findByTitle("Page 2");
        User user = userRepository.findByMail("unittest@marugoto.ch");
    	PageState pageState = pageStateService.initializeStateForNewPage(page, user);
    	exerciseStateService.initializeStateForNewPage(pageState);
        
    	var exerciseStates = exerciseStateRepository.findByPageStateId(pageState.getId());
		assertFalse(exerciseStates.isEmpty());
		assertThat (exerciseStates.get(0).getExercise(), instanceOf(CheckboxExercise.class));
    }
    
    @Test
    public void testUpdateExerciseState() {
    	String inputState = "updatedState";
        Page page = pageRepository.findByTitle("Page 1");
        User user = userRepository.findByMail("unittest@marugoto.ch");
        PageState pageState = pageStateService.initializeStateForNewPage(page, user);
        exerciseStateService.initializeStateForNewPage(pageState);
       
        var exerciseStates = exerciseStateRepository.findByPageStateId(pageState.getId());
        ExerciseState updatedExerciseState = exerciseStateService.updateExerciseState(exerciseStates.get(0).getId(), inputState);
    	assertThat(updatedExerciseState.getInputState(), is(inputState));
    }
    
    @Test
    public void testExerciseCriteriaIsSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        TextExercise textExercise = new TextExercise(2, 0, 10, "Can you test exercise?");
        textExercise.addTextSolution(new TextSolution("yes", TextSolutionMode.fullmatch));
        ExerciseState exerciseState = new ExerciseState(textExercise, "yes");
        // true
        var satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, ExerciseCriteriaType.correctInput);
        assertTrue(satisfied);
        // false
        satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, ExerciseCriteriaType.incorrectInput);
        assertFalse(satisfied);
        // true
        exerciseState.setInputState("");
        satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
        // true
        exerciseState.setInputState(null);
        satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
    }
}