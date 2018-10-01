package ch.uzh.marugoto.backend.request;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.service.UserService;


@Component
public class RequestValidation implements Validator {

	
	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisterUser.class.isAssignableFrom(clazz);
	}
	@Override
	public void validate(Object target, Errors errors) {
		
		RegisterUser user = (RegisterUser) target;
		ValidationUtils.rejectIfEmpty(errors, "firstName", "Please fill your firstName");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "Please fill your lastName");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "","Email is empty");
		if (!user.getMail().contains("@")) {
			errors.rejectValue("mail","", "Email is not valid.");
		}
		var userExists = userService.getUserByMail(user.getMail());	
		if (userExists != null) {
			errors.rejectValue("mail","", "There is already a user registered with the email provided");

		}
		boolean validatePassword = validatePassword(user.getPassword());
		if (!validatePassword) {
			errors.rejectValue("password","","Please check your password. It must contain at least 8 digits with 1 capital letter,and 1 digit.\"");
		}
	}
	
	public boolean validatePassword(String password) { 
		Pattern pattern = java.util.regex.Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,16})");
    	Matcher matcher = pattern.matcher(password);
    	return matcher.matches();
    }
	
}
