package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.service.NotificationService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;

public class MailServiceTest extends BaseCoreTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;

    public synchronized void before() {
        super.before();
    }

    @Test
    public void testGetIncomingMails() {
        var page3 = pageRepository.findByTitle("Page 6");
        var mailList = mailService.getIncomingMails(page3);
        assertEquals(1, mailList.size());
    }

    @Test
    public void testReceivedMails() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mailList = mailService.getReceivedMails(user);
        assertEquals(1, mailList.size());
        assertEquals("Page 1", mailList.get(0).getPage().getTitle());
    }

    @Test
    public void testReplyOnMail() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mailList = mailService.getIncomingMails(pageRepository.findByTitle("Page 1"));
        mailService.replyOnMail(user, mailList.get(0).getId(), "Replied mail page 1");

        var received = mailService.getReceivedMails(user);
        assertEquals("Replied mail page 1", received.get(0).getReplied().getText());
    }

    @Test
    public void testReceiveMail() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mailList = mailService.getIncomingMails(pageRepository.findByTitle("Page 6"));
        assertEquals(1, mailService.getReceivedMails(user).size());

        mailService.receiveMail(mailList.get(0).getId(), user);
        assertEquals(2, mailService.getReceivedMails(user).size());
    }
}
