package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.RepliedMail;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.RepliedMailNotFoundException;
import ch.uzh.marugoto.core.service.NotificationService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class FakeEmailServiceTest extends BaseCoreTest {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private NotificationService notificationService;
	@Autowired
    private PageStateService pageStateService;
    private PageState pageState6;
    private RepliedMail repliedMail;
	
	public synchronized void before() {
		super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var page6 = pageRepository.findByTitle("Page 6");
        pageState6 = pageStateService.initializeStateForNewPage(page6, user);
        var mail = notificationService.getMails(page6).get(0);
        repliedMail = new RepliedMail(mail, pageState6);
        repliedMail.setPageState(pageState6);
    	notificationService.saveRepliedMail(repliedMail);
    }
	
	@Test
	public void testGetAllMailExercises() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var repliedMails = notificationService.getAllRepliedMails(user);
        assertThat(repliedMails.get(0), instanceOf(RepliedMail.class));
		assertEquals(repliedMails.size(), 1);
	}
	
	@Test 
	public void testGetMailExerciseById() throws RepliedMailNotFoundException {
		var mail = notificationService.getMails(pageState6.getPage()).get(0);
		var repliedMail = notificationService.getRepliedMail(pageState6.getUser(), mail.getId());
		assertThat(repliedMail, instanceOf(RepliedMail.class));
		assertEquals(repliedMail.getMail().getId(), mail.getId());
	}
	
	@Test
	public void testSendEmail() {
		var mail = notificationService.getMails(pageState6.getPage()).get(0);
		var repliedMail = notificationService.sendReplyMail(pageState6.getUser(), mail, "Reply text");
		assertEquals("Reply text", repliedMail.getText());
	}
}
