package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import ch.uzh.marugoto.backend.resource.PasswordForget;
import ch.uzh.marugoto.backend.resource.PasswordReset;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.RegisterUser;
import ch.uzh.marugoto.backend.exception.RequestValidationException;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.exception.UserNotFoundException;
import ch.uzh.marugoto.core.service.MailableService;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;


@RestController
@Validated
public class UserController extends BaseController {

	@Autowired
	private MailableService mailableService;
	@Autowired
	private UserService userService;

	@ApiOperation(value = "Creates new user")
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public User register(@Validated @RequestBody RegisterUser registeredUser, BindingResult result) throws RequestValidationException, DtoToEntityException {
		if (result.hasErrors()) {
			throw new RequestValidationException(result.getFieldErrors());
		}

		return userService.createUser(registeredUser);
	}

	@ApiOperation(value = "Finds user by email and generates token")
	@RequestMapping(value = "/user/password-forget", method = RequestMethod.POST)
	public HashMap<String, String> forgotPassword(@Validated @RequestBody PasswordForget passwordForget, BindingResult result)
			throws Exception {
		var objectMap = new HashMap<String, String>();
		User user = userService.getUserByMail(passwordForget.getEmail());
		if (result.hasErrors()) {
			throw new RequestValidationException(result.getFieldErrors());
		}
		if (user == null) {
			throw new RequestValidationException("userNotFound.forEmail");
		}

		String resetPasswordLink = userService.getResetPasswordLink(user, passwordForget.getPasswordResetUrl());
		mailableService.sendResetPasswordEmail(user.getMail(), resetPasswordLink);

		objectMap.put("resetToken", user.getResetToken());
		return objectMap;
	}

	@ApiOperation(value = "Set new password for user")
	@RequestMapping(value = "/user/password-reset", method = RequestMethod.POST)
	public User resetPassword(@Validated @RequestBody PasswordReset passwordReset, BindingResult result) throws RequestValidationException {
		if (result.hasErrors()) {
			throw new RequestValidationException(result.getFieldErrors());
		}

		try {
			return userService.updatePassword(passwordReset.getToken(), passwordReset.getNewPassword());
		} catch (UserNotFoundException e) {
			throw new RequestValidationException("userNotFound.forResetToken");
		}
	}
}
