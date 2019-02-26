package ch.uzh.marugoto.core.data.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator implements ConstraintValidator<Password, String> {
		
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		return validatePassword(password);
	}
	
	public boolean validatePassword(String password) { 
		Pattern pattern = Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,16})");
    	Matcher matcher = pattern.matcher(password);
    	return matcher.matches();
    }
}
