package ch.uzh.marugoto.backend.test.service;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageState;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageStateRepository;
import ch.uzh.marugoto.backend.service.StateService;
import ch.uzh.marugoto.backend.test.BaseTest;

public class StateServiceTest extends BaseTest {
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private StateService stateService;
	
	
	@Test
	public void testGetPageStateByPageId() {
		
		var page = pageRepository.save(new Page("Page with state 1", true, null));
		var state = pageStateRepository.save(new PageState(page));
		
		assertEquals(PageState.class, stateService.getPageState(page.getId()).getClass());
		assertEquals(state.getId(), stateService.getPageState(page.getId()).getId());
	}
}
