package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple test cases for PageTransitionStateRepository.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageTransitionStateRepositoryTest extends BaseCoreTest {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Autowired
	private PageTransitionStateRepository pageTransitionStateRepository;
	
	@Test
	public void testFindByPageTransitionAndUser() {
		var pageTransition = pageTransitionRepository.getPageTransitionsByPageId(pageRepository.findByTitle("Page 1").getId()).get(0);
		var user = userRepository.findByMail("unittest@marugoto.ch");

		var pageTransitionState = pageTransitionStateRepository.findByPageTransitionAndUser(pageTransition.getId(), user.getId());

		assertNotNull(pageTransitionState);
		assertEquals(pageTransitionState.getPageTransition().getId(), pageTransition.getId());
		assertEquals(pageTransitionState.getUser().getId(), user.getId());
	}
}
