package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

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
