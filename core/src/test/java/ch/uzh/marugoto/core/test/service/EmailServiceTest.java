package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.service.EmailServiceImpl;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class EmailServiceTest extends BaseCoreTest {

	@Autowired
	private EmailServiceImpl emailService;

	@Test
	public void testSendResetPasswordEmail () {
		String resetLink = "http://localhost/api/user/password-reset?token=6b653aed-f601-4d50-8fa4-40bb132ff7b1";
		String toAddress = "pera@live.com";
		emailService.sendResetPasswordEmail(toAddress, resetLink);
	}
}
