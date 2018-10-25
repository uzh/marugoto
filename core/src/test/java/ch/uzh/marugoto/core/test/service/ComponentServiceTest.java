package ch.uzh.marugoto.core.test.service;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {
	
	@Autowired
	private ExerciseService exerciseService;
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
	}
	

	
	@Test
	public void testParseMarkdownToHtml(){
		String markdownText = "This is **Sparta**";
		String htmlText = exerciseService.parseMarkdownToHtml(markdownText);
		assertEquals("<p>This is <strong>Sparta</strong></p>\n", htmlText);
	}
}
