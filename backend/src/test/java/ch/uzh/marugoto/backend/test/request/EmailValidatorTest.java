package ch.uzh.marugoto.backend.test.request;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.uzh.marugoto.backend.validation.EmailValidator;

@ActiveProfiles("testing")
@SpringBootTest
@RunWith(SpringRunner.class)
public class EmailValidatorTest {

	@Autowired
	private EmailValidator emailValidator;
	
	@Test
	public void testValidateEmail() {
		
		String whthoutAtSign = "dadada.da";
		String basicString = "dadadada";
		String validMail = "testa@test.ch";
		boolean incorrectwhthoutAt = emailValidator.validateEmail(whthoutAtSign);
		boolean incorrectBasicString = emailValidator.validateEmail(basicString);
		boolean correct = emailValidator.validateEmail(validMail);
		
		assertFalse(incorrectwhthoutAt);
		assertFalse(incorrectBasicString);
		assertTrue(correct);
	}
}
