package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
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
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
		var page = pageRepository.findByTitle("Page 2");
		textExercise = (TextExercise) page.getComponents().get(0);
	}
	
	@Test
	public void test1CheckTextExerciseWithContainsComparisonMode() {
		textExercise.getTextSolutions().get(0).setMode(TextSolutionMode.contains);
		boolean solution1 = componentService.checkExercise(textExercise, "Thank you");
		boolean solution2 = componentService.checkExercise(textExercise, "Thank y");
		boolean solution3 = componentService.checkExercise(textExercise, "thank's");
		boolean solution4 = componentService.checkExercise(textExercise, "thanks");

		assertTrue(solution1);
		assertFalse(solution2);
		assertTrue(solution3);
		assertFalse(solution4);
	}
	
	@Test
	public void test2CheckTextExerciseWithFullmatchComparisonMode() {
		textExercise.getTextSolutions().get(0).setMode(TextSolutionMode.fullmatch);
		boolean solution1 = componentService.checkExercise(textExercise, "Thank you");
		boolean solution2 = componentService.checkExercise(textExercise, "Fuck you");
		boolean solution3 = componentService.checkExercise(textExercise, "thank's");
		boolean solution4 = componentService.checkExercise(textExercise, "tak's");

		assertTrue(solution1);
		assertFalse(solution2);
		assertTrue(solution3);
		assertFalse(solution4);
	}
}
