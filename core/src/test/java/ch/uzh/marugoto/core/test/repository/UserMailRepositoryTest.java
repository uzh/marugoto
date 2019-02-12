package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.state.UserMail;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class UserMailRepositoryTest extends BaseCoreTest {
    @Autowired
    private UserMailRepository userMailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void testFindAllByUserId() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mails = userMailRepository.findAllByUserId(user.getId());
        assertEquals(1, mails.size());
    }

    @Test
    public void testFindByUserIdAndMailId() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mailNotification = notificationRepository.findMailNotifications().get(0);

        userMailRepository.save(new UserMail(mailNotification, user));
        var mail = userMailRepository.findByUserIdAndMailId(user.getId(), mailNotification.getId()).orElse(null);
        assertNotNull(mail);
        assertEquals(mail.getMail().getId(), mailNotification.getId());
    }
}
