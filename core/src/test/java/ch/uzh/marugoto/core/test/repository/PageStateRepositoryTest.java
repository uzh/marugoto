package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple test cases for PageStateRepository.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageStateRepositoryTest extends BaseCoreTest {

	@Autowired
	private PageStateRepository pageStateRepository;

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test1CreatePageState() {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var storylineState = user.getCurrentlyPlaying();
		var state = pageStateRepository.save(new PageState(page, storylineState));

		assertNotNull(state);
		assertEquals(page.getId(), state.getPage().getId());
		assertEquals(page.getTitle(), state.getPage().getTitle());
	}

	@Test
	public void test2FindByPageAndStorylineState() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageStateRepository.findByPageAndStorylineState(page.getId(), user.getCurrentlyPlaying().getId());

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), "Page 1");
	}
}
