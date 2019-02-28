package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MailServiceTest extends BaseCoreTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private MailStateRepository mailStateRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PageTransitionRepository pageTransitionRepository;
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
        user.setCurrentPageState(new PageState(page6));
        var mailList = mailService.getIncomingMails(user);
        assertEquals(1, mailList.size());
    }

    @Test
    public synchronized void testReceivedMails() throws InterruptedException {
        var mailStateList = mailService.getReceivedMails(user);
        assertEquals(1, mailStateList.size());
        assertEquals("Page 1", mailStateList.get(0).getMail().getPage().getTitle());

        wait(500);
        mailStateRepository.save(new MailState(incomingMailsPage6.get(0), user));
        mailStateList = mailService.getReceivedMails(user);
        assertEquals(2, mailStateList.size());
        assertEquals("Page 6", mailStateList.get(0).getMail().getPage().getTitle());
    }

    @Test
    public void testReplyOnMail() {
        mailStateRepository.save(new MailState(incomingMailsPage6.get(0), user));
        var mailState = mailService.replyOnMail(user, incomingMailsPage6.get(0).getId(), "Replied mail page 6");

        assertEquals(1, mailState.getMailReplyList().size());
        assertEquals("Replied mail page 6", mailState.getMailReplyList().iterator().next().getBody());
        // test sort - newest on the top
        mailState = mailService.replyOnMail(user, incomingMailsPage6.get(0).getId(), "Another reply comes first in the list");
        assertEquals(2, mailState.getMailReplyList().size());
        assertNotEquals("Replied mail page 6", mailState.getMailReplyList().iterator().next().getBody());
    }

    @Test
    public void testHasMailReplyTransition() {
        var pageTransition = mailService.getMailReplyTransition(incomingMailsPage6.get(0).getId(), user.getCurrentPageState());
        assertNull(pageTransition);

        var mailList = notificationRepository.findMailNotificationsForPage(pageRepository.findByTitle("Page 1").getId());
        pageTransition = mailService.getMailReplyTransition(mailList.get(0).getId(), user.getCurrentPageState());
        assertNotNull(pageTransition);

        // change mail criteria type so test not pass
        for (Criteria criteria : pageTransition.getCriteria()) {
            if (criteria.isForMail()) {
                criteria.setMailCriteria(MailCriteriaType.read);
            }
        }
        pageTransitionRepository.save(pageTransition);

        pageTransition = mailService.getMailReplyTransition(mailList.get(0).getId(), user.getCurrentPageState());
        assertNull(pageTransition);
    }

    @Test
    public void testSyncMail() {
        assertEquals(1, mailService.getReceivedMails(user).size());
        // mail received
        var mail = mailService.updateMailState(incomingMailsPage6.get(0).getId(), user, false);
        assertEquals(2, mailService.getReceivedMails(user).size());
        assertFalse(mail.isRead());
        // mail has been read
        mail = mailService.updateMailState(incomingMailsPage6.get(0).getId(), user, true);
        assertEquals(2, mailService.getReceivedMails(user).size());
        assertTrue(mail.isRead());
    }

    @Test
    public void testGetMailNotifications() {
        assertEquals(1, incomingMailsPage6.size());
    }

    @Test
    public void testGetMailNotification() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var method = MailService.class.getDeclaredMethod("getMailNotification", String.class);
        method.setAccessible(true);

        var testMail = incomingMailsPage6.get(0);
        var mail = (Mail) method.invoke(mailService, testMail.getId());
        assertNotNull(mail);
    }

    @Test
    public void testGetFormattedText() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var method = MailService.class.getDeclaredMethod("getFormattedText", String.class, User.class);
        method.setAccessible(true);
        var textToCheck = method.invoke(mailService, "Dear {{user.name}} you have received this test mail by accident!", user);
        assertEquals(textToCheck, "Dear Sir Fredi Kruger you have received this test mail by accident!");
    }
}
