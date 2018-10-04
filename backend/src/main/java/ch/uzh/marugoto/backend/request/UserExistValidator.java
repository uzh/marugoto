package ch.uzh.marugoto.backend.request;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.service.UserService;

@Component
public class UserExistValidator implements ConstraintValidator<UserExist, String> {

	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean isValid(String userEmail, ConstraintValidatorContext context) {
		var userExist = userService.getUserByMail(userEmail);
		return userExist == null;
	}
}
