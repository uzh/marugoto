package ch.uzh.marugoto.backend.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.PageState;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageStateRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Page state repository test class
 *
 */
public class PageStateRepositoryTest extends BaseTest {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private PageRepository pageRepository;

	@Test
	public void testCreatePageState() {

		var title = "Page 1";
		var page = pageRepository.findByTitle(title); 
		var state = pageStateRepository.save(new PageState(page));

		assertNotNull(state);
		assertEquals(title, state.getPage().getTitle());
	}
}
