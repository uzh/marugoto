
package ch.uzh.marugoto.backend.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
		var pages = Lists.newArrayList(pageRepository.findAll());

		assertTrue(pages.size() == Lists.newArrayList(pageService.getAllPages()).size());
	}

	@Test
	public void testGetPageById() {
		
		var pages = Lists.newArrayList(pageRepository.findAll());
		var pageId = pages.get(pages.size() - 1).getId();

		assertEquals(pageId, pageService.getPage(pageId).getId());
	}
	
}
