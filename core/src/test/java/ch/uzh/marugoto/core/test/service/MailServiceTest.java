package ch.uzh.marugoto.core.test.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class MailServiceTest extends BaseCoreTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    private User user;
    private Page page6;
    private List<Mail> incomingMailsPage6;

    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        page6 = pageRepository.findByTitle("Page 6");
        incomingMailsPage6 = notificationRepository.findMailNotificationsForPage(page6.getId());
    }

    @Test
    public void testGetIncomingMails() {
        var page6 = pageRepository.findByTitle("Page 6");
        var mailList = mailService.getIncomingMails(new PageState(page6, user));
        assertEquals(1, mailList.size());
    }

    @Test
    public void testReceivedMails() {
        var mailStateList = mailService.getReceivedMails(user);
        assertEquals(1, mailStateList.size());
        assertEquals("Page 1", mailStateList.get(0).getMail().getPage().getTitle());
    }

    @Test
    public void testReplyOnMail() {
        var mailList = notificationRepository.findMailNotificationsForPage(page6.getId());
        var mailState = mailService.replyOnMail(user, mailList.get(0).getId(), "Replied mail page 6");

        assertEquals(1, mailState.getMailReplyList().size());
        assertEquals("Replied mail page 6", mailState.getMailReplyList().get(0).getBody());
    }

    @Test
    public void testSyncMail() {
        assertEquals(1, mailService.getReceivedMails(user).size());
        // mail received
        var mail = mailService.syncMail(incomingMailsPage6.get(0).getId(), user, false);
        assertEquals(2, mailService.getReceivedMails(user).size());
        assertFalse(mail.isRead());
        // mail has been read
        mail = mailService.syncMail(incomingMailsPage6.get(0).getId(), user, true);
        assertEquals(2, mailService.getReceivedMails(user).size());
        assertTrue(mail.isRead());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMailNotifications() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var method = MailService.class.getDeclaredMethod("getMailNotifications", Page.class);
        method.setAccessible(true);
        var notifications = (List<Mail>) method.invoke(mailService, page6);
        assertEquals(1, notifications.size());
    }

    @Test
    public void testGetMailNotification() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var method = MailService.class.getDeclaredMethod("getMailNotification", String.class);
        method.setAccessible(true);

        var testMail = notificationRepository.findMailNotificationsForPage(page6.getId()).get(0);
        var mail = (Mail) method.invoke(mailService, testMail.getId());
        assertNotNull(mail);
    }
}
