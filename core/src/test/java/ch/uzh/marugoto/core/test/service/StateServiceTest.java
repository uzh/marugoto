package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StateServiceTest extends BaseCoreTest {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StateService stateService;

	
	@Test
	public void testPageState() {
		// Create
		var page = pageRepository.save(new Page("Page State 1", true, null));
		var user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"));
		
		var pageState = stateService.createPageStage(page, user);
		
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "fred.dark@test.com");
		
		// Load
		var page2 = pageRepository.findByTitle("Page State 1");
		var user2 = userRepository.findByMail("fred.dark@test.com");

		var pageState2 = stateService.getPageState(page2, user2);
		assertNotNull(pageState2);
		assertEquals(pageState2.getUser().getMail(), "fred.dark@test.com");
		assertEquals(pageState2.getPage().getTitle(), "Page State 1");
	}
}
