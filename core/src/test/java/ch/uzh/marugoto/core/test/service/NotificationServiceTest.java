package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.NotificationService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;

public class NotificationServiceTest extends BaseCoreTest {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private NotificationService notificationService;
    private Page page3;

	public synchronized void before() {
		super.before();
        page3 = pageRepository.findByTitle("Page 3");
    }

	@Test
	public void testGetPageNotifications() {
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
	public void testGetMailNotifications() {
		var mailNotifications = notificationService.getIncomingMails();
		assertEquals(Mail.class, mailNotifications.get(0).getClass());
	}
}
