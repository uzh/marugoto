package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.uzh.marugoto.core.data.entity.Dialog;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.NotificationService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class NotificationServiceTest extends BaseCoreTest {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private UserRepository userRepository;
    private Page page3;


	public synchronized void before() {
		super.before();
        page3 = pageRepository.findByTitle("Page 3");
    }

	@Test
	public void testGetIncomingNotifications() {
		var notifications = notificationService.getIncomingNotifications(page3);
		assertEquals(1, notifications.size());
	}

	@Test
	public void testGetNotification() {
		var notification = notificationService.getIncomingNotifications(page3).get(0);
		var testNotification = notificationService.getNotification(notification.getId());
		assertEquals(notification.getId(), testNotification.getId());
	}

	@Test
	public void testGetIncomingMails() {
		var mailNotifications = notificationService.getIncomingMails();
		assertEquals(Mail.class, mailNotifications.get(0).getClass());
	}

	@Test
	public void testGetIncomingMailsForPage() {
		var mailNotifications = notificationService.getIncomingMails(pageRepository.findByTitle("Page 6"));
		assertEquals(Mail.class, mailNotifications.get(0).getClass());
		assertEquals(1, mailNotifications.size());
	}

	@Test
	public void testGetIncomingDialogsForPage() {
		var dialogs = notificationService.getIncomingDialogs(page3);
		assertEquals(1, dialogs.size());
		assertEquals(dialogs.get(0).getClass(), Dialog.class);
	}

	@Test
	public void testReplaceTextInMailBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var mail = notificationService.getIncomingMails().get(0);
		mail.setBody("Hi {{user.name}}, " + mail.getBody());

		assertFalse(mail.getBody().contains(user.getName()));

		Method method = NotificationService.class.getDeclaredMethod("replaceUserNameTextInMailBody", Mail.class, User.class);
		method.setAccessible(true);
		method.invoke(notificationService, mail, user);

		assertTrue(mail.getBody().contains(user.getName()));
	}
}
