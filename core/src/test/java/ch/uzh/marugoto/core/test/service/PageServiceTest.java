package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
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

	@Test
	public void test1GetPageById() {
		var page = pageRepository.save(new Page("Test Page 1", false, null));
		var testPage = pageService.getPage(page.getId());

		assertNotNull(testPage);
		assertEquals(testPage.getId(), page.getId());
	}
	
	@Test
	public void test2DoTransition() {
		// TODO after Dusan to pushes base core test changes 
//		var page = pageRepository.findByTitle("Test Page 1");
//		var pageTransition = pageService.getPageTransitions(page.getId()).get(0);
//		
//		var nextPage = pageService.doTransition(pageTransition.getId(), userRepository.findByMail("fred.dark@test.com"));
//		
//		assertNotNull(nextPage);
//		assertEquals(pageTransition.getTo().getId(), nextPage.getId());
	}
}
