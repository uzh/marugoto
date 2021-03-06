package ch.uzh.marugoto.core.data.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.service.UserService;

@Component
public class UserNotExistValidator implements ConstraintValidator<UserNotExist, String> {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean isValid(String userEmail, ConstraintValidatorContext context) {
		var userExist = userService.getUserByMail(userEmail);
		return userExist == null;
	}
}
