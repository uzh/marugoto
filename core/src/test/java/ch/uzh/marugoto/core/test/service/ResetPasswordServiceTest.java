package ch.uzh.marugoto.core.test.service;

import java.util.UUID;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.PasswordService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ResetPasswordServiceTest extends BaseCoreTest {

	@Autowired
	private PasswordService emailService;
	@Autowired
	private UserRepository userRepository;

	@Test
	public void testSendResetPasswordEmail () throws MessagingException {
		String passwordResetUrl = "http://localhost/api/user/password-reset";
		User user = userRepository.findByMail("unittest@marugoto.ch");
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		emailService.sendResetPasswordEmail(user, passwordResetUrl);
	}
}
