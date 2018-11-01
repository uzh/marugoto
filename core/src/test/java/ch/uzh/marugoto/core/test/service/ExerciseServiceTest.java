package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ExerciseServiceTest extends BaseCoreTest {

    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private PageRepository pageRepository;

	@Test
	public void testGetExercises () {
		var page1 = pageRepository.findByTitle("Page 1");
		var exercises1 = exerciseService.getExercises(page1);
		assertThat(exercises1.get(0), instanceOf(TextExercise.class));
		
		var page2 = pageRepository.findByTitle("Page 2");
		var exercises2 = exerciseService.getExercises(page2);
		assertThat(exercises2.get(0), instanceOf(CheckboxExercise.class));
		
		var page3 = pageRepository.findByTitle("Page 4");
		var exercises3 = exerciseService.getExercises(page3);
		assertThat(exercises3.size(), is(2));
	}
	
	@Test
	public void testHasExercise () {
		var page = pageRepository.findByTitle("Page 1");
		boolean hasExercise = exerciseService.hasExercise(page);
		assertTrue(hasExercise);
	}

    @Test
    public void testCheckboxExerciseForMaxSelection () {
        var page = pageRepository.findByTitle("Page 2");
        var checkboxExerciseForMax = exerciseService.getExercises(page).get(0);
        boolean testMax = exerciseService.checkExercise(checkboxExerciseForMax,"1,3,4");
        assertTrue(testMax);
    }

    @Test
    public void testCheckboxExerciseForMinSelection () {
        var page = pageRepository.findByTitle("Page 3");
        var checkboxExerciseForMin = exerciseService.getExercises(page).get(0);
        boolean testMin = exerciseService.checkExercise(checkboxExerciseForMin,"2");
        assertFalse(testMin);
    }

    @Test
    public void testTextExercise() {
        var page = pageRepository.findByTitle("Page 1");
        var textExercise = exerciseService.getExercises(page).get(0);
        boolean testContains = exerciseService.checkExercise(textExercise,"Thanks you");
        assertTrue(testContains);

        boolean testFullMatch = exerciseService.checkExercise(textExercise,"Thank you");
        assertTrue(testFullMatch);

        boolean testFuzzyMatch = exerciseService.checkExercise(textExercise,"Thanks you");
        assertTrue(testFuzzyMatch);
    }

    @Test
    public void testRadioButtonExercise () {
        var page = pageRepository.findByTitle("Page 4");
        var radioButtonExercise = exerciseService.getExercises(page)
                .stream()
                .filter(exercise -> exercise instanceof RadioButtonExercise)
                .findFirst().orElseThrow();
        
        assertTrue(exerciseService.checkExercise(radioButtonExercise, "3"));
    }

    @Test
    public void testDateExercise () {
        String time = "2018-12-06 12:32";
        var page = pageRepository.findByTitle("Page 4");
        var dateExercise = exerciseService.getExercises(page)
                .stream()
                .filter(exercise -> exercise instanceof DateExercise)
                .findFirst().orElseThrow();
        
        assertTrue(exerciseService.checkExercise(dateExercise, time));
    }
}
