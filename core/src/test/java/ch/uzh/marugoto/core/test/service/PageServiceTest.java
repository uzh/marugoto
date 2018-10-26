package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
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
	private UserRepository userRepository;

	private User user;

	@Before
	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
	}

	@Test
	public void testGetPageById() {
		var page1Id = pageRepository.findByTitle("Page 1").getId();
		var testPage = pageService.getPage(page1Id);

		assertNotNull(testPage);
		assertEquals("Page 1", testPage.getTitle());
		assertEquals(2, testPage.getComponents().size());
	}
}
