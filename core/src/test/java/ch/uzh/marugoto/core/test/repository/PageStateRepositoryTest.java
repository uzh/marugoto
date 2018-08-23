package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PageStateRepositoryTest extends BaseCoreTest {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private PageRepository pageRepository;

	@Test
	public void testCreatePageState() {
		var page = pageRepository.save(new Page("Page 1", false, null));
		var state = pageStateRepository.save(new PageState(page));

		assertNotNull(state);
		assertEquals(page.getId(), state.getPage().getId());
	}
}
