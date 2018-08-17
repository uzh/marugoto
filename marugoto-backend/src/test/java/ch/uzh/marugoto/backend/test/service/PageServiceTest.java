
package ch.uzh.marugoto.backend.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.backend.service.PageService;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Tests for PageService.
 */
public class PageServiceTest extends BaseTest {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	
	@Test
	public void testGetAllPages() {
		
		pageRepository.save(new Page("Test Page 1", true, null));	
		var size = Lists.newArrayList(pageRepository.findAll()).size();

		assertTrue(size == Lists.newArrayList(pageService.getAllPages()).size());
	}

	@Test
	public void testGetPageById() {
		var page = pageRepository.save(new Page("Test Page 2", false, null));
		var testPage = pageService.getPage(page.getId().split("/")[1]);

		assertNotNull(testPage);
		assertEquals(testPage.getId(), page.getId());
	}
	
}
