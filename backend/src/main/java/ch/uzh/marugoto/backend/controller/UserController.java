package ch.uzh.marugoto.backend.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import ch.uzh.marugoto.backend.resource.PasswordForget;
import ch.uzh.marugoto.backend.resource.PasswordReset;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.RequestValidationException;
import ch.uzh.marugoto.core.service.EmailServiceImpl;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;


@RestController
@Validated
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private CoreConfiguration coreConfig;
	@Autowired
	private EmailServiceImpl emailService;
	@Autowired
	private Messages messages;

	@ApiOperation(value = "Creates new user")
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public User register(@Validated @RequestBody RegisterUser registredUser, BindingResult result) throws RequestValidationException, IllegalAccessException, InvocationTargetException, JsonProcessingException, ParseException {
		User user = new User();
		if (result.hasErrors()) {
			throw new RequestValidationException(handleValidationErrors(result.getFieldErrors()));

		} else {
			BeanUtils.copyProperties(user, registredUser);
			user.setPasswordHash(coreConfig.passwordEncoder().encode(registredUser.getPassword()));
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
		emailService.sendResetPasswordEmail(user.getMail(), resetLink);

		objectMap.put("resetToken", user.getResetToken());
		return objectMap;
	}

	@ApiOperation(value = "Set new password for user")
	@RequestMapping(value = "/user/password-reset", method = RequestMethod.POST)
	public User resetPassword(@Validated @RequestBody PasswordReset passwordReset, BindingResult result) throws Exception {
		User user = userService.findUserByResetToken(passwordReset.getToken(), passwordReset.getUserEmail());
		if (result.hasErrors()) {
			throw new RequestValidationException(handleValidationErrors(result.getFieldErrors()));
		}
		if (user == null || !user.getMail().equals(passwordReset.getUserEmail())) {
			throw new RequestValidationException(messages.get("userNotFound.forResetToken"));
		}
		
		user.setPasswordHash(coreConfig.passwordEncoder().encode(passwordReset.getNewPassword()));
		user.setResetToken(null);
		userService.saveUser(user);

		return user;
	}
}
