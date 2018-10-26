package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
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
		assertThat(exercises3.get(1), instanceOf(RadioButtonExercise.class));
		
		var page4 = pageRepository.findByTitle("Page 4");
		var exercises4 = exerciseService.getExercises(page4);
		assertThat(exercises4.get(0), instanceOf(DateExercise.class));
	}
	
	@Test
	public void testHasExercise () {
		var page = pageRepository.findByTitle("Page 1");
		boolean hasExercise = exerciseService.hasExercise(page);
		assertTrue(hasExercise);
	}
}
