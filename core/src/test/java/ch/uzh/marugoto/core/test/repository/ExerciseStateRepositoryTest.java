package ch.uzh.marugoto.core.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Option;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
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
    private PageRepository pageRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private StateService pageStateService;

    private PageState pageState;

    @Before
    public synchronized void before() {
        super.before();
        var page = pageRepository.findByTitle("Page 2");
        var user = userRepository.findByMail("unittest@marugoto.ch");
        pageState = pageStateService.getState(page, user);
    }

    @Test
    public void testFindUserExerciseStates() {
        var exerciseStates = exerciseStateRepository.findByPageStateId(pageState.getId());
        assertEquals(1, exerciseStates.size());
    }

    @Test
    public void testFindExerciseState() {
        List<Option> options = Arrays.asList(new Option("1"), new Option ("2") , new Option ("3"), new Option ("4"));
        var testRadioButtonExercise = new RadioButtonExercise(3, options,3, pageState.getPage());
        var exercise = componentRepository.save(testRadioButtonExercise);
        exerciseStateRepository.save(new ExerciseState(testRadioButtonExercise, "1,3", pageState));

        var stateToTest = exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();

        assertNotNull(stateToTest);
        assertTrue(stateToTest.getExercise() instanceof RadioButtonExercise);
        assertEquals(stateToTest.getExercise().getId(), exercise.getId());
    }
}
