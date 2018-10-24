package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.service.EmailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertTrue;

public class EmailServiceTest extends BaseCoreTest {

//	@Autowired
//	private EmailService emailService;

	
	// TODO test for EmailService
	
	@Test
	public void testSendEmail () throws Exception {
//		String resetLink = "http://localhost/api/user/password-reset?token=6b653aed-f601-4d50-8fa4-40bb132ff7b1";
//		String fromAddress = "no-reply@memorynotfound.com";
//		String toAddress = "pera@live.com";
//		emailService.sendEmail(toAddress, fromAddress, resetLink);
		assertTrue(true);
	}
	
}
