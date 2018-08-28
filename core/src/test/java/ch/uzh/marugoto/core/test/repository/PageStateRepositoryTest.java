package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PageStateRepositoryTest extends BaseCoreTest {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;

	
	@Test
	public void testCreatePageState() {
		var page = pageRepository.save(new Page("PageState 1", true, null));
		var user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Page", "State", "page.state@test.com", "test"));
		
		var state = pageStateRepository.save(new PageState(page, user));

		assertNotNull(state);
		assertEquals(page.getId(), state.getPage().getId());
		assertEquals(page.getTitle(), state.getPage().getTitle());
	}
}
