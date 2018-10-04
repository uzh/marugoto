package ch.uzh.marugoto.core.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
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
    private StateService stateService;

    private PageState pageState;

    @Before
    public synchronized void before() {
        super.before();
        var page = pageRepository.findByTitle("Page 2");
        var user = userRepository.findByMail("unittest@marugoto.ch");
        pageState = stateService.getPageState(page, user);
    }

    @Test
    public void testFindExerciseStates() {
        var exerciseStates = stateService.getExerciseStates(pageState);
        assertEquals(exerciseStates.size(), 1);
    }

    @Test
    public void testFindExerciseState() {
        var exercise = stateService.getExerciseStates(pageState).get(0).getExercise();
        var stateToTest = exerciseStateRepository.findExerciseState(pageState.getId(), exercise.getId()).orElseThrow();

        assertNotNull(stateToTest);
        assertTrue(stateToTest.getExercise() instanceof TextExercise);
        assertEquals(stateToTest.getExercise().getId(), exercise.getId());
    }
}
