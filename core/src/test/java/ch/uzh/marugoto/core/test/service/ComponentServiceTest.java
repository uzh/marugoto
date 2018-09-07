package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {
	
	@Autowired
	private ComponentService componentService;
	
	@Autowired
	private PageRepository pageRepository;
	
	private TextExercise textExercise;
	
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
		var page = pageRepository.findByTitle("Page 2");
		textExercise = (TextExercise) page.getComponents().get(0);
	}
	
	@Test
	public void testCheckTextExercise() {
		var exerciseState = new ExerciseState(textExercise,"Thanks you");
		exerciseStateRepository.save(exerciseState);
		boolean testContaints = componentService.checkExercise(exerciseState);
		assertTrue(testContaints);
		
		exerciseState.setInputText("Thank you");
		exerciseStateRepository.save(exerciseState);
		boolean testFullMatch = componentService.checkExercise(exerciseState);
		assertTrue(testFullMatch);
		
		exerciseState.setInputText("Thanks you");
		exerciseStateRepository.save(exerciseState);
		boolean testFuzzyMatch = componentService.checkExercise(exerciseState);
		assertTrue(testFuzzyMatch);
	}
}
