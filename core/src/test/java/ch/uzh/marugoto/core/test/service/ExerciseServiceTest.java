package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExerciseServiceTest extends BaseCoreTest {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private PageRepository pageRepository;


    @Autowired
    private ExerciseStateRepository exerciseStateRepository;

    @Test
    public void testExerciseCriteriaIsSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ExerciseService.class.getDeclaredMethod("exerciseCriteriaSatisfied", ExerciseState.class, ExerciseCriteriaType.class);
        method.setAccessible(true);

        TextExercise textExercise = new TextExercise(2, 0, 10, "Can you test exercise?");
        textExercise.addTextSolution(new TextSolution("yes", TextSolutionMode.fullmatch));
        ExerciseState exerciseState = new ExerciseState(textExercise, "yes");

        // true
        var satisfied = (boolean) method.invoke(exerciseService, exerciseState, ExerciseCriteriaType.correctInput);
        assertTrue(satisfied);
        // false
        satisfied = (boolean) method.invoke(exerciseService, exerciseState, ExerciseCriteriaType.incorrectInput);
        assertFalse(satisfied);
        // true
        exerciseState.setInputState("");
        satisfied = (boolean) method.invoke(exerciseService, exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
        // true
        exerciseState.setInputState(null);
        satisfied = (boolean) method.invoke(exerciseService, exerciseState, ExerciseCriteriaType.noInput);
        assertTrue(satisfied);
        // false
        satisfied = (boolean) method.invoke(exerciseService, exerciseState, ExerciseCriteriaType.correctInput);
        assertFalse(satisfied);
    }

    @Test
    public void testCheckboxExerciseForMaxSelection () {
        var page = pageRepository.findByTitle("Page 2");
        var checkboxExerciseForMax = exerciseService.getExercises(page).get(0);
        var exerciseStateForMax = new ExerciseState(checkboxExerciseForMax,"1,3,4");
        exerciseStateRepository.save(exerciseStateForMax);
        boolean testMax = exerciseService.isCheckboxExerciseCorrect(exerciseStateForMax);
        assertTrue(testMax);
    }

    @Test
    public void testCheckboxExerciseForMinSelection () {
        var page = pageRepository.findByTitle("Page 3");
        var checkboxExerciseForMin = exerciseService.getExercises(page).get(0);
        var exerciseStateForMin = new ExerciseState(checkboxExerciseForMin,"2");
        exerciseStateRepository.save(exerciseStateForMin);
        boolean testMin = exerciseService.isCheckboxExerciseCorrect(exerciseStateForMin);
        assertFalse(testMin);
    }

    @Test
    public void testCheckTextExercise() {
        var page = pageRepository.findByTitle("Page 1");
        var textExercise = exerciseService.getExercises(page).get(0);
        var exerciseState = new ExerciseState(textExercise,"Thanks you");
        exerciseStateRepository.save(exerciseState);
        boolean testContains = exerciseService.isTextExerciseCorrect(exerciseState);
        assertTrue(testContains);

        exerciseState.setInputState("Thank you");
        exerciseStateRepository.save(exerciseState);
        boolean testFullMatch = exerciseService.isTextExerciseCorrect(exerciseState);
        assertTrue(testFullMatch);

        exerciseState.setInputState("Thanks you");
        exerciseStateRepository.save(exerciseState);
        boolean testFuzzyMatch = exerciseService.isTextExerciseCorrect(exerciseState);
        assertTrue(testFuzzyMatch);
    }

    @Test
    public void testRadioButtonExercise () {
        var page = pageRepository.findByTitle("Page 4");
        var radioButtonExercise = exerciseService.getExercises(page)
                .stream()
                .filter(exercise -> exercise instanceof RadioButtonExercise)
                .findFirst().orElseThrow();

        var exerciseState = new ExerciseState(radioButtonExercise,"3");
        exerciseStateRepository.save(exerciseState);

        assertTrue(exerciseService.isRadioButtonExerciseCorrect(exerciseState));
    }

    @Test
    public void testDateExercise () {
        String time = "2018-12-06 12:32";
        var page = pageRepository.findByTitle("Page 4");
        var dateExercise = exerciseService.getExercises(page)
                .stream()
                .filter(exercise -> exercise instanceof DateExercise)
                .findFirst().orElseThrow();

        var exerciseState = new ExerciseState(dateExercise, time);
        exerciseStateRepository.save(exerciseState);

        assertTrue(exerciseService.isDateExerciseCorrect(exerciseState));
    }
}
