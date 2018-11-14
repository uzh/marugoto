package ch.uzh.marugoto.backend.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator implements ConstraintValidator<EmailNotValid, String> {

	
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		return validateEmail(email);
	}
	
	public boolean validateEmail(String email) { 
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$");
    	Matcher matcher = pattern.matcher(email);
    	return matcher.matches();
    }

}

