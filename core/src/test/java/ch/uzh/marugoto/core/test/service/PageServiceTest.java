package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

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

	@Test
	public void test1IsPageStateCreatedWhenItIsMissing() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageService.getPageState(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page.getTitle());
	}

	@Test
	public void testDoTransition() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 1");
		var pageState = pageService.getPageState(page, user);
		pageState.getPageTransitionStates().get(0).setAvailable(true);
		pageStateRepository.save(pageState);


		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		var pageTransition = pageTransitions.get(0);
		var nextPage = pageService.doTransition(true, pageTransition.getId(), user);
		
		assertNotNull(nextPage);
		assertEquals(pageTransition.getTo().getId(), nextPage.getId());
	}

	@Test(expected = PageTransitionNotAllowedException.class)
	public void testDoTransitionWhenIsNotAllowed() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 2");
		// init page state
		pageService.getPageState(page, user);
		var transitions = pageTransitionRepository.findByPageId(page.getId());

		pageService.doTransition(false, transitions.get(0).getId(), user);
	}




}
