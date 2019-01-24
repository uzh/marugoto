package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Tests for PageService.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageServiceTest extends BaseCoreTest {

	@Autowired
	private PageService pageService;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private ComponentService componentService;
	
	@Test
	public void testGetPageById() {
		var page1 = pageRepository.findByTitle("Page 1");
		page1.setComponents(componentService.getPageComponents(page1));
		var testPage = pageService.getPage(page1.getId());

		assertNotNull(testPage);
		assertEquals(page1.getTitle(), testPage.getTitle());
		assertEquals(page1.getComponents().size(), testPage.getComponents().size());
	}
	
	@Test
	public void testGetTopicStartPage() {
		Page page = pageService.getTopicStartPage();
		assertNotNull(page);
	}
}
