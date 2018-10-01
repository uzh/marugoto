package ch.uzh.marugoto.backend.controller;


import java.util.HashMap;

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.request.RequestValidation;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;


@RestController
public class RegistrationController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private RequestValidation request;

	@InitBinder
	    private void initBinder(WebDataBinder binder) {
	        binder.setValidator(request);
    }	
	
	@ApiOperation(value = "Creates new user", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)	
	public HashMap<String, String> register(@Valid @RequestBody RegisterUser registredUser, BindingResult result) throws Exception {   
		
		var objectMap = new HashMap<String, String>();
		if(result.hasErrors()) {
			for (var i = 0; i < result.getErrorCount(); i++) {
				objectMap.put(result.getFieldErrors().get(i).getField().toString(), result.getFieldErrors().get(i).getDefaultMessage());	
			}
	    }else {
	    	User user =  new User();
			BeanUtils.copyProperties(user, registredUser);
			userService.createUser(user);
			objectMap.put("status", HttpStatus.OK.toString());
	    }
		return objectMap;

	}
}
