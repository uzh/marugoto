package ch.uzh.marugoto.backend.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.PageState;
import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.entity.UserType;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageStateRepository;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Simple tests for PageState entity
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageStateRepositoryTest extends BaseTest {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Test
	public void test1CreatePageState() {

		var title = "Page 1";
		var page = pageRepository.findByTitle(title);
		var user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Page", "State", "page.state@test.com", "test"));
		

		var state = pageStateRepository.save(new PageState(page, user));

		assertNotNull(state);
		assertEquals(title, state.getPage().getTitle());
	}
}
