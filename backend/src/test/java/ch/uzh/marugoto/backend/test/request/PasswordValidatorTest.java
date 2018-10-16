package ch.uzh.marugoto.backend.test.request;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.uzh.marugoto.backend.validation.PasswordValidator;


@ActiveProfiles("testing")
@SpringBootTest
@RunWith(SpringRunner.class)
public class PasswordValidatorTest {

	@Autowired
	private PasswordValidator password;
	
	@Test
	public void testValidatePassword() {

		String correctPassword = "Mypassword8";
		String noCapitalLetter = "nocapitalletter";
		String noDigit = "letterWithoutDigit";
		String not8letters = "letter";
		
		boolean correct = password.validatePassword(correctPassword);
		boolean capital = password.validatePassword(noCapitalLetter);
		boolean digit = password.validatePassword(noDigit);
		boolean numberOfletters = password.validatePassword(not8letters);

		assertTrue(correct);
		assertFalse(capital);
		assertFalse(digit);
		assertFalse(numberOfletters);
	}
}
