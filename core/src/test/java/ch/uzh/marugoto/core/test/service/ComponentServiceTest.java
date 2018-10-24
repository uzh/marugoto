package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {
	
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private PageRepository pageRepository;

	
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
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
	
	@Test
	public void testParseMarkdownToHtml(){
		String markdownText = "This is **Sparta**";
		String htmlText = exerciseService.parseMarkdownToHtml(markdownText);
		assertEquals("<p>This is <strong>Sparta</strong></p>\n", htmlText);
	}
}
