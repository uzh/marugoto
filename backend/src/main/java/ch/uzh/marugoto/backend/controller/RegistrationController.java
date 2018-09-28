package ch.uzh.marugoto.backend.controller;


import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;


@RestController
public class RegistrationController extends BaseController {

	@Autowired
	private UserService userService;
	
	@ApiOperation(value = "Creates new user", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)	
	public ResponseEntity<?> register(@Valid @RequestBody RegisterUser registred) throws Exception {   
		
		User user =  new User();       
		User userExists = userService.getUserByMail(registred.getMail());	
		if (userExists != null) {
			throw new Exception("There is already a user registered with the email provided.\"");
		}
		boolean validatePassword = userService.validatePassword(registred.getPassword());
		if (!validatePassword) {
			throw new Exception("Plese check your password. It must contain at least 8 digits with 1 capital letter,and 1 digit.\"");
		}
		BeanUtils.copyProperties(user, registred);
		userService.createUser(user);			
		return ResponseEntity.ok(user);
	}
}
