package ch.uzh.marugoto.backend.controller;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import ch.uzh.marugoto.backend.request.Password;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;

@RestController
@Validated
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CoreConfiguration coreConfig;
	
	@ApiOperation(value = "Creates new user")
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)	
	public ResponseEntity<?> register(@Validated @RequestBody RegisterUser registredUser, BindingResult result) throws Exception {   
		
		var objectMap = new HashMap<String, String>();
		if(result.hasErrors()) {
			return new ResponseEntity<>(handleValidationExceptions(result), HttpStatus.BAD_REQUEST);
		} else {
	    	User user =  new User();
			BeanUtils.copyProperties(user, registredUser);
			userService.saveUser(user);
			objectMap.put("status", HttpStatus.OK.toString());
	    }
		return ResponseEntity.ok(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/password-forget", method = RequestMethod.POST)		
	public HashMap<String, String> forgotPassword (
			@Email @RequestParam("mail") String userEmail, HttpServletRequest request) throws Exception {
		
		User user = userService.getUserByMail(userEmail);
		var objectMap = new HashMap<String, String>();
		if (user == null) {
			throw new Exception("There is no user registered with the email provided");
		}
		user.setResetToken(UUID.randomUUID().toString());
		userService.saveUser(user);
		String appUrl = request.getScheme() + "://" + request.getServerName();	
		objectMap.put("resetLink", appUrl + "/api/user/password-reset?token=" + user.getResetToken());

		return objectMap;
	}
	
	@RequestMapping(value = "/user/password-reset", method = RequestMethod.POST)
	public String resetPassword(@Password @RequestParam("newPassword") String password, @RequestParam("token") String token) throws Exception {
		User user = userService.findUserByResetToken(token);
		String message = null;
		if (user == null) {
			throw new Exception("This is invalid password reset link");
		}
		user.setPasswordHash(coreConfig.passwordEncoder().encode(password));
		user.setResetToken(null);
		userService.saveUser(user);
		message = "You have successfully reset your password";
		
		return message;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<String> handleValidationExceptions(BindingResult result) {
	    return result
	        .getAllErrors().stream()
	        .map(ObjectError::getDefaultMessage)
	        .collect(Collectors.toList());
	}
	
	
}
