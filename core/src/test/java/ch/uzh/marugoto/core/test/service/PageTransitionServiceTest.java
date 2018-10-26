package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PageTransitionServiceTest extends BaseCoreTest {
    
    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }
}
