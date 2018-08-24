package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the StateService class
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StateServiceTest extends BaseCoreTest {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StateService stateService;

	@Test
	public void test1CreatePageState() {
		// Create
		var page = pageRepository.save(new Page("Page State 1", true, null));
		var user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"));		
		var pageState = stateService.createPageStage(page, user);
		
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "fred.dark@test.com");
		
		// Load
		var loadedPageState = stateService.getPageState(page, user);

		assertNotNull(loadedPageState);
		assertEquals(loadedPageState.getId(),pageState.getId());
		assertEquals(pageState.getUser().getMail(), "fred.dark@test.com");
	}

}
