package ch.uzh.marugoto.core.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseOption;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExerciseStateRepositoryTest extends BaseCoreTest {
    
	@Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private ComponentRepository componentRepository;
    private PageState pageState;

    @Before
    public synchronized void before() {
        super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
        pageState = user.getCurrentPageState();  
    }

    @Test
    public void testFindUserExerciseStates() {
        var exerciseStates = exerciseStateRepository.findByPageStateId(pageState.getId());
        assertEquals(1, exerciseStates.size());
    }

    @Test
    public void testFindExerciseState() {
        List<ExerciseOption> options = Arrays.asList(
                new ExerciseOption("one", false),
                new ExerciseOption("two", true),
                new ExerciseOption("three", true),
                new ExerciseOption("four", false));

        var testRadioButtonExercise = new RadioButtonExercise(3, options, pageState.getPage());
        var exercise = componentRepository.save(testRadioButtonExercise);
        exerciseStateRepository.save(new ExerciseState(testRadioButtonExercise, "1,3", pageState));

        var stateToTest = exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();

        assertNotNull(stateToTest);
        assertTrue(stateToTest.getExercise() instanceof RadioButtonExercise);
        assertEquals(stateToTest.getExercise().getId(), exercise.getId());
    }
    
}
