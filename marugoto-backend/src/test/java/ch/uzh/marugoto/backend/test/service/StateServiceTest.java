package ch.uzh.marugoto.backend.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.entity.UserType;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageStateRepository;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.service.StateService;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * 
 * Simple Tests for StateService class
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StateServiceTest extends BaseTest {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StateService stateService;

	@Test
	public void test1CreatePageState() {
		Page page = pageRepository.save(new Page("Page State 1", true, null));
		User user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"));
		
		var pageState = stateService.createPageStage(page, user);
		
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "fred.dark@test.com");
	}
	
	@Test
	public void test2GetPageState() {
		var page = pageRepository.findByTitle("Page State 1");
		var user = userRepository.findByMail("fred.dark@test.com");

		var pageState = stateService.getPageState(page, user);
		assertNotNull(pageState);
		assertEquals(pageState.getUser().getMail(), "fred.dark@test.com");
		assertEquals(pageState.getPage().getTitle(), "Page State 1");
	}
}
