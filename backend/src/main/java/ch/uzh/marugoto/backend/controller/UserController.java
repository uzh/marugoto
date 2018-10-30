package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.backend.validation.Password;
import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.User;
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
	public User register(@Validated @RequestBody RegisterUser registredUser, BindingResult result) throws Exception {

		User user = new User();
		if (result.hasErrors()) {
			throw new Exception(result.getAllErrors().stream().map(e -> e.toString()).reduce("", String::concat));
			// return new ResponseEntity<>(handleValidationExceptions(result),
			// HttpStatus.BAD_REQUEST);
		} else {
			BeanUtils.copyProperties(user, registredUser);
			userService.saveUser(user);
		}
		return user;
	}

	@ApiOperation(value = "Finds user by email and generates token")
	@RequestMapping(value = "/user/password-forget", method = RequestMethod.POST)
	public HashMap<String, String> forgotPassword(@Email @RequestParam("mail") String userEmail, @RequestParam("passwordResetUrl") String passwordResetUrl)
			throws Exception {

		User user = userService.getUserByMail(userEmail);
		var objectMap = new HashMap<String, String>();
		if (user == null) {
			throw new Exception(messages.get("userNotFound.forEmail"));
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
	public User resetPassword(@Email @RequestParam("mail") String userEmail, @RequestParam("token") String token, @Password @RequestParam("newPassword") String password)
			throws Exception {

		User user = userService.findUserByResetToken(token);
		if (user == null || !user.getMail().equals(userEmail)) {
			throw new Exception(messages.get("userNotFound.forResetToken"));
		}
		user.setPasswordHash(coreConfig.passwordEncoder().encode(password));
		user.setResetToken(null);
		userService.saveUser(user);

		return user;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<String> handleValidationExceptions(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
	}

}
