package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExerciseServiceTest extends BaseCoreTest {

    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private PageRepository pageRepository;
    private Page page1;
    private Page page2;
    private Page page3;
    private Page page4;

    @Before
    public synchronized void before() {
        super.before();
        page1 = pageRepository.findByTitle("Page 1");
        page2 = pageRepository.findByTitle("Page 2");
        page3 = pageRepository.findByTitle("Page 3");
        page4 = pageRepository.findByTitle("Page 4");
    }

	@Test
	public void testGetExercises () {
		var exercises1 = exerciseService.getExercises(page1);
		assertThat(exercises1.get(0), instanceOf(TextExercise.class));

		var exercises2 = exerciseService.getExercises(page3);
		assertThat(exercises2.get(0), instanceOf(CheckboxExercise.class));

		var exercises3 = exerciseService.getExercises(page4);
		assertThat(exercises3.size(), is(1));
	}
	
	@Test
	public void testHasExercise () {
		boolean hasExercise = exerciseService.hasExercise(page1);
		assertTrue(hasExercise);
	}

    @Test
    public void testCheckboxExercise() {
        var checkboxExercise = exerciseService.getExercises(page3).get(0);
        boolean correct = exerciseService.checkExercise(checkboxExercise,"1,2");
        boolean notCorrect = exerciseService.checkExercise(checkboxExercise,"1,3");
        assertTrue(correct);
        assertFalse(notCorrect);
    }

    @Test
    public void testTextExercise() {
        var textExercise = exerciseService.getExercises(page1).get(0);
        boolean testContains = exerciseService.checkExercise(textExercise,"Thanks you");
        assertTrue(testContains);

        boolean testFullMatch = exerciseService.checkExercise(textExercise,"Thank you");
        assertTrue(testFullMatch);

        boolean testFuzzyMatch = exerciseService.checkExercise(textExercise,"Thanks you");
        assertTrue(testFuzzyMatch);
    }

    @Test
    public void testRadioButtonExercise () {
        var radioButtonExercise = exerciseService.getExercises(page2)
                .stream()
                .filter(exercise -> exercise instanceof RadioButtonExercise)
                .findFirst().orElseThrow();
        
        assertTrue(exerciseService.checkExercise(radioButtonExercise, "2"));
    }

    @Test
    public void testDateExercise () {
        String time = "06.12.2018";
        var dateExercise = exerciseService.getExercises(page4)
                .stream()
                .filter(exercise -> exercise instanceof DateExercise)
                .findFirst().orElseThrow();
        
        assertTrue(exerciseService.checkExercise(dateExercise, time));
    }
}
