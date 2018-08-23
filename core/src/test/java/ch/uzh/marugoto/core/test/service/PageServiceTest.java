
package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.helper.EntityHelper;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Tests for PageService.
 */
public class PageServiceTest extends BaseCoreTest {

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
