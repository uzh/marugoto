package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.TextExercise;
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
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		textExercise = (TextExercise) pages.get(1).getComponents().get(0);
	}
	
	@Test
	public void testCheckboxExerciseForMaxSelection () {
		var checkboxExerciseForMax = pageRepository.findByTitle("Page 3").getComponents().get(0);
		var exerciseStateForMax = new ExerciseState((CheckboxExercise)checkboxExerciseForMax,"1,3,4");
		exerciseStateRepository.save(exerciseStateForMax);
		boolean testMax = componentService.isCheckboxExerciseCorrect(exerciseStateForMax);
		assertTrue(testMax);	
	}
	
	@Test
	public void testCheckboxExerciseForMinSelection () {
		var checkboxExerciseForMin = pageRepository.findByTitle("Page 3").getComponents().get(1);
		var exerciseStateForMin = new ExerciseState((CheckboxExercise)checkboxExerciseForMin,"2");
		exerciseStateRepository.save(exerciseStateForMin);
		boolean testMin = componentService.isCheckboxExerciseCorrect(exerciseStateForMin);
		assertFalse(testMin);		
	}
	
	@Test
	public void testCheckTextExercise() {
		var exerciseState = new ExerciseState(textExercise,"Thanks you");
		exerciseStateRepository.save(exerciseState);
		boolean testContaints = componentService.isTextExerciseCorrect(exerciseState);
		assertTrue(testContaints);
		
		exerciseState.setInputState("Thank you");
		exerciseStateRepository.save(exerciseState);
		boolean testFullMatch = componentService.isTextExerciseCorrect(exerciseState);
		assertTrue(testFullMatch);
		
		exerciseState.setInputState("Thanks you");
		exerciseStateRepository.save(exerciseState);
		boolean testFuzzyMatch = componentService.isTextExerciseCorrect(exerciseState);
		assertTrue(testFuzzyMatch);
	}
	
	@Test
	public void testParseMarkdownToHtml(){
		String markdownText = "This is **Sparta**";
		String htmlText = componentService.parseMarkdownToHtml(markdownText);
		assertEquals("<p>This is <strong>Sparta</strong></p>\n", htmlText);
	}
}
