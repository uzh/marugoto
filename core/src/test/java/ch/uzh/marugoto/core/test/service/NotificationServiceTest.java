package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Dialog;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserMail;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.UserMailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;

public class NotificationServiceTest extends BaseCoreTest {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private UserMailService userMailService;
	@Autowired
	private UserMailRepository userMailRepository;
	@Autowired
    private PageStateService pageStateService;
    private User user;
    private Page page6;
    private Page page3;
    private Mail mail;

	public synchronized void before() {
		super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        page6 = pageRepository.findByTitle("Page 6");
        page3 = pageRepository.findByTitle("Page 3");
        var pageState6 = pageStateService.initializeStateForNewPage(page6, user);
        mail = userMailService.getMailNotifications(page6).get(0);
		var repliedMail = new UserMail(mail, pageState6);
        repliedMail.setPageState(pageState6);
		userMailRepository.save(repliedMail);
    }

    @Test
	public void testGetPageNotification() {
		var notificationList = userMailService.getPageNotifications(page6);
		assertEquals(1, notificationList.size());
		assertEquals(Mail.class, notificationList.get(0).getClass());

		var notificationList2 = userMailService.getPageNotifications(page3);
		assertEquals(1, notificationList.size());
		assertEquals(Dialog.class, notificationList2.get(0).getClass());
	}

    @Test
	public void testGetMails() {
		var mailNotifications = userMailService.getMailNotifications(page6);
		assertEquals(Mail.class, mailNotifications.get(0).getClass());

		var noMailNotifications = userMailService.getMailNotifications(page3);
		assertEquals(0, noMailNotifications.size());
	}

	@Test
	public void testFindMail() {
		var mailToTest = userMailService.getMailNotification(mail.getId());
		assertEquals(mail.getId(), mailToTest.getId());
	}

	@Test
	public void testReplyMail() {
		var repliedMail = userMailService.replyMail(user, mail.getId(), "Testing reply");
		assertEquals(UserMail.class, repliedMail.getClass());
		assertEquals(user.getId(), repliedMail.getPageState().getUser().getId());
	}

	@Test
	public void getAllMailsWithUserReplies() {
		userMailService.replyMail(user, mail.getId(), "another reply");
		var mails = userMailService.getAllMailsWithUserReplies(user);

		assertEquals(1, mails.size());
		assertEquals(2, mails.get(0).getReplies().size());
	}

	@Test
	public void testGetDialogs() {
		var dialogs = userMailService.getDialogNotifications(page3);
		assertEquals(1, dialogs.size());
		assertEquals(Dialog.class, dialogs.get(0).getClass());

		dialogs = userMailService.getDialogNotifications(page6);
		assertEquals(0, dialogs.size());
	}
}
