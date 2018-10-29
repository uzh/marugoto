package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PageStateServiceTest extends BaseCoreTest {

    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StateService pageStateService;

    
    @Test
	public void testGetState() {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageStateService.getState(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page.getTitle());
	}
    
	@Test
	public void testGetStateCreateStateIfNotExist() {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageState = pageStateService.getState(page, user);

		assertNotNull(pageState);
		assertEquals(pageState.getPage().getTitle(), page.getTitle());
	}
    
    
    @Test
    public void testCreatePageState() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var page = pageRepository.findByTitle("Page 1");
        var user = userRepository.findByMail("unittest@marugoto.ch");

        Method method = StateService.class.getDeclaredMethod("createState", Page.class, User.class);
        method.setAccessible(true);

        var pageState = (PageState) method.invoke(pageStateService, page, user);

        assertNotNull(pageState);
        assertEquals(pageState.getPage().getId(), page.getId());
    }
}
