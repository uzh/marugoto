package ch.uzh.marugoto.backend.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.resource.PasswordReset;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.backend.validation.EmailNotValid;
import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.RequestValidationException;
import ch.uzh.marugoto.core.service.EmailService;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;


@RestController
@Validated
public class UserController extends BaseController {

	static final String marugotoEmail = "test@marugoto.ch";
	@Autowired
	private UserService userService;
	@Autowired
	private CoreConfiguration coreConfig;
	@Autowired
	private EmailService emailService;
	@Autowired
	private Messages messages;

	@ApiOperation(value = "Creates new user")
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public User register(@Validated @RequestBody RegisterUser registredUser, BindingResult result) throws RequestValidationException, IllegalAccessException, InvocationTargetException {

		User user = new User();
		if (result.hasErrors()) {
			throw new RequestValidationException(result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()+ " ").reduce("", String::concat));
		} else {
			BeanUtils.copyProperties(user, registredUser);
			userService.saveUser(user);
		}
		return user;
	}

	@ApiOperation(value = "Finds user by email and generates token")
	@RequestMapping(value = "/user/password-forget", method = RequestMethod.POST)
	public HashMap<String, String> forgotPassword(@EmailNotValid @RequestParam("mail") String userEmail, @RequestParam("passwordResetUrl") String passwordResetUrl)
			throws Exception {
	
		User user = userService.getUserByMail(userEmail);
		var objectMap = new HashMap<String, String>();
		if (user == null) {
			throw new RequestValidationException(messages.get("userNotFound.forEmail"));
		}
		user.setResetToken(UUID.randomUUID().toString());
		userService.saveUser(user);

		String resetLink = passwordResetUrl + "?token=" + user.getResetToken();
		emailService.sendEmail(userEmail, marugotoEmail, resetLink);

		objectMap.put("resetToken", user.getResetToken());
		return objectMap;
	}

	@ApiOperation(value = "Set new password for user")
	@RequestMapping(value = "/user/password-reset", method = RequestMethod.POST)
	public User resetPassword(@Validated @RequestBody PasswordReset passwordReset, BindingResult result) throws Exception {

		if (result.hasErrors()) {
			throw new RequestValidationException(result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()+ " ").reduce("", String::concat));
		}
		User user = userService.findUserByResetToken(passwordReset.getToken(), passwordReset.getUserEmail());
		user.setPasswordHash(coreConfig.passwordEncoder().encode(passwordReset.getNewPassword()));
		user.setResetToken(null);
		userService.saveUser(user);

		return user;
	}

}
