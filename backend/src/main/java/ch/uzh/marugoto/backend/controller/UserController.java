package ch.uzh.marugoto.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.beanutils.BeanUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

import ch.uzh.marugoto.backend.resource.PasswordForget;
import ch.uzh.marugoto.backend.resource.PasswordReset;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.exception.RequestValidationException;
import ch.uzh.marugoto.core.service.PasswordService;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;


@RestController
@Validated
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private PasswordService passwordService;

	@ApiOperation(value = "Creates new user")
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public User register(@Validated @RequestBody RegisterUser registeredUser, BindingResult result) throws RequestValidationException, IllegalAccessException, InvocationTargetException, JsonProcessingException, ParseException {
		User user = new User();
		if (result.hasErrors()) {
			throw new RequestValidationException(handleValidationErrors(result.getFieldErrors()));

		} else {
			BeanUtils.copyProperties(user, registeredUser);
			user.setPasswordHash(passwordService.getEncodedPassword(registeredUser.getPassword()));
			userService.saveUser(user);
		}
		return user;
	}

	@ApiOperation(value = "Finds user by email and generates token")
	@RequestMapping(value = "/user/password-forget", method = RequestMethod.POST)
	public HashMap<String, String> forgotPassword(@Validated @RequestBody PasswordForget passwordForget, BindingResult result)
			throws Exception {
		var objectMap = new HashMap<String, String>();
		User user = userService.getUserByMail(passwordForget.getEmail());
		if (result.hasErrors()) {
			throw new RequestValidationException(handleValidationErrors(result.getFieldErrors()));
		}
		if (user == null) {
			throw new RequestValidationException(messages.get("userNotFound.forEmail"));
		}
		user.setResetToken(UUID.randomUUID().toString());
		userService.saveUser(user);

		String resetLink = passwordForget.getPasswordResetUrl() + "?token=" + user.getResetToken();
		passwordService.sendResetPasswordEmail(user.getMail(), resetLink);

		objectMap.put("resetToken", user.getResetToken());
		return objectMap;
	}

	@ApiOperation(value = "Set new password for user")
	@RequestMapping(value = "/user/password-reset", method = RequestMethod.POST)
	public User resetPassword(@Validated @RequestBody PasswordReset passwordReset, BindingResult result) throws Exception {
		User user = userService.findUserByResetToken(passwordReset.getToken());
		if (result.hasErrors()) {
			throw new RequestValidationException(handleValidationErrors(result.getFieldErrors()));
		}
		if (user == null || !user.getMail().equals(passwordReset.getUserEmail())) {
			throw new RequestValidationException(messages.get("userNotFound.forResetToken"));
		}
		
		user.setPasswordHash(passwordService.getEncodedPassword(passwordReset.getNewPassword()));
		user.setResetToken(null);
		userService.saveUser(user);

		return user;
	}
}
