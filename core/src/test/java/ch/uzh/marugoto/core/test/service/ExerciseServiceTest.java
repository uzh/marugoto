package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExerciseServiceTest extends BaseCoreTest {

    @Autowired
    private ExerciseService exerciseService;

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
}
