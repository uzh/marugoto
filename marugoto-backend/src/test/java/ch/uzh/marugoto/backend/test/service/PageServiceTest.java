
package ch.uzh.marugoto.backend.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.helper.EntityHelper;
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

	@Test
	public void testGetPageById() {
		var page = pageRepository.save(new Page("Test Page 2", false, null));
		var testPage = pageService.getPage(EntityHelper.getNumericId(page.getId()));

		assertNotNull(testPage);
		assertEquals(testPage.getId(), page.getId());
	}
}
