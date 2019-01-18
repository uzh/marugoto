package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.service.ExerciseService;
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
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
	}
	
	@Test
	public void testGetPageComponents() {
		var page = pageRepository.findByTitle("Page 1");
		var components = componentService.getPageComponents(page);
		assertFalse(components.isEmpty());
	}
	
	@Test
	public void testParseMarkdownToHtml() {
		String markdownText = "This is **Sparta**";
		String htmlText = componentService.parseMarkdownToHtml(markdownText);
		assertEquals("<p>This is <strong>Sparta</strong></p>\n", htmlText);
	}
}
