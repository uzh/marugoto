package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PageStateServiceTest extends BaseCoreTest {

    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageStateService pageStateService;
    @Autowired
    private PageStateRepository pageStateRepository;
    
    @Test
	public void testInitializeStateForNewPage() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageStateService.initializeStateForNewPage(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page.getTitle());
	}
    
	@Test
	public void testGetPageState() throws PageStateNotFoundException {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		@SuppressWarnings({ "deprecation", "unused" })
		var pageState = pageStateService.getPageState(user);
	}
    
    @Test
    public void testGetPageStates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var page1 = pageRepository.findByTitle("Page 2");
        var user = userRepository.findByMail("unittest@marugoto.ch");
		pageStateService.initializeStateForNewPage(page1, user);
		var pageStates = pageStateService.getPageStates(user);
		
        assertNotNull(pageStates);
        assertEquals(pageStates.size(), 2);
    }
    
    @Test
    public void testSetLeftAt () {
    	var page = pageRepository.findByTitle("Page 1");
        var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageStateService.initializeStateForNewPage(page, user);
		pageStateService.setLeftAt(pageState);
		assertNotNull(pageState.getLeftAt());
    }
    
    @Test
    public void testSavePageState() {
    	var page = pageRepository.findByTitle("Page 1");
        var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = new PageState(page, user);
		pageStateService.savePageState(pageState);
		assertNotNull(pageStateRepository.findById(pageState.getId()));
    }
}