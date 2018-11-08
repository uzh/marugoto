package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
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

    private Page page1;
    private Page page2;
    private User user;

    @Before
    public synchronized void before() {
        super.before();
        page1 = pageRepository.findByTitle("Page 1");
        page2 = pageRepository.findByTitle("Page 2");
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
	public void testInitializeStateForNewPage() {
		var pageState = pageStateService.initializeStateForNewPage(page1, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page1.getTitle());
	}
    
	@Test
	public void testGetPageState() throws PageStateNotFoundException {
		@SuppressWarnings({ "deprecation", "unused" })
		var pageState = pageStateService.getPageState(user);
	}
    
    @Test
    public void testGetPageStates() {
		pageStateService.initializeStateForNewPage(page2, user);
		var pageStates = pageStateService.getPageStates(user);
		
        assertNotNull(pageStates);
        assertEquals(pageStates.size(), 2);
    }
    
    @Test
    public void testSetLeftAt () {
		var pageState = pageStateService.initializeStateForNewPage(page1, user);
		pageStateService.setLeftAt(pageState);
		assertNotNull(pageState.getLeftAt());
    }
    
    @Test
    public void testSavePageState() {
		var pageState = new PageState(page1, user);
		pageStateService.savePageState(pageState);
		assertNotNull(pageStateRepository.findById(pageState.getId()));
    }
}