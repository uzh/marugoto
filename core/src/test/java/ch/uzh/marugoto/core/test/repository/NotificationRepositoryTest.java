package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class NotificationRepositoryTest extends BaseCoreTest {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PageRepository pageRepository;

    @Test
    public void testFindMailNotification() {
        var mails = notificationRepository.findMailNotificationsForPage(pageRepository.findByTitle("Page 6").getId());
        var mailNotification = notificationRepository.findMailNotification(mails.get(0).getId());
        assertNotNull(mailNotification);
    }

    @Test
    public void testFindMailNotificationsForPage() {
        var mails = notificationRepository.findMailNotificationsForPage(pageRepository.findByTitle("Page 6").getId());
        assertThat(mails.isEmpty(), is(false));
        assertEquals(mails.get(0).getClass(), Mail.class);
        // no mail notifications
        mails = notificationRepository.findMailNotificationsForPage(pageRepository.findByTitle("Page 3").getId());
        assertThat(mails.isEmpty(), is(true));
    }

    @Test
    public void testFindDialogNotificationForPage() {
        var dialogs = notificationRepository.findDialogNotificationsForPage(pageRepository.findByTitle("Page 3").getId());
        assertThat(dialogs.isEmpty(), is(false));
        assertEquals(dialogs.get(0).getClass(), Dialog.class);
        // no dialogs
        dialogs = notificationRepository.findDialogNotificationsForPage(pageRepository.findByTitle("Page 6").getId());
        assertThat(dialogs.isEmpty(), is(true));
    }
}
