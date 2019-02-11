package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
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
	private User user;

	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
	}

	@Test
	public void test1CreatePageState() {
		var page = pageRepository.findByTitle("Page 2");
		var state = pageStateRepository.save(new PageState(page, user));

		assertNotNull(state);
		assertEquals(page.getId(), state.getPage().getId());
		assertEquals(page.getTitle(), state.getPage().getTitle());
	}

	@Test
	public void test2FindAllByUser() {
		List<PageState> pageStateList = pageStateRepository.findUserPageStates(user.getId());
		assertFalse(pageStateList.isEmpty());
	}
}
