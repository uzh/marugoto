package ch.uzh.marugoto.core.test.repository;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class MailStateRepositoryTest extends BaseCoreTest {
    @Autowired
    private MailStateRepository mailStateRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAllByUserId() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var mails = mailStateRepository.findAllByUserId(user.getId());
        assertEquals(1, mails.size());
    }

    @Test
    public void testFindMailState() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var testMailState = mailStateRepository.findAllByUserId(user.getId()).get(0);

        var mailState = mailStateRepository.findMailState(user.getId(), testMailState.getMail().getId()).orElse(null);
        assertNotNull(mailState);
        assertEquals(testMailState.getId(), mailState.getId());
    }
}
