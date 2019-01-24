package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ch.uzh.marugoto.backend.resource.PasswordForget;
import ch.uzh.marugoto.backend.resource.PasswordReset;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.repository.UserRepository;

@AutoConfigureMockMvc
public class UserControllerTest extends BaseControllerTest{
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Messages messages;
	
	@Test
	@JsonSerialize
	public void testRegister() throws Exception {
		
		RegisterUser user = new RegisterUser(Salutation.Mr,"Fredi","Kruger","fredi@maal.com","Pasword1");
		String content = new ObjectMapper().writeValueAsString(user);

		mvc.perform(post("/api/user/registration")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    	.andExpect(status().is(200));		
	}

	@Test
	public void testForgotPassword() throws Exception {
		
		PasswordForget passwordForgetModel = new PasswordForget("unittest@marugoto.ch","/api/user/password-reset");
		String content = new ObjectMapper().writeValueAsString(passwordForgetModel);
		mvc.perform(post("/api/user/password-forget")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    	.andExpect(status().is(200));
	}
	
	@Test
	public void testForgotPasswordIfMailIsWrong() throws Exception {
		PasswordForget passwordForgetModel = new PasswordForget("test@marugoto.ch","/api/user/password-reset");
		String content = new ObjectMapper().writeValueAsString(passwordForgetModel);
		mvc.perform(post("/api/user/password-forget")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    	.andExpect(status().is(400))
		.andExpect(jsonPath("$.message", is(messages.get("userNotFound.forEmail"))));
	}
	
	@Test
	public void testResetPassword() throws Exception{
		var userEmail = "unittest@marugoto.ch";
		var user = userRepository.findByMail(userEmail);
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		
		PasswordReset passwodResetModel = new PasswordReset(userEmail,user.getResetToken(),"NewPasswod1");
		String content = new ObjectMapper().writeValueAsString(passwodResetModel);
		
		mvc.perform(post("/api/user/password-reset")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    		.andExpect(status().is(200));
	}
	
	@Test
	public void testResetPasswordIfPasswordIsWrong() throws Exception{
		var userEmail = "unittest@marugoto.ch";
		var user = userRepository.findByMail(userEmail);
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		
		PasswordReset passwodResetModel = new PasswordReset(userEmail,user.getResetToken(),"wrongPassword");
		String content = new ObjectMapper().writeValueAsString(passwodResetModel);
		
		mvc.perform(post("/api/user/password-reset")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    	.andExpect(status().is(400));
	}
	
	@Test
	public void testResetPasswordIfTokenIsWrong() throws Exception{

		PasswordReset passwodResetModel = new PasswordReset("unittest@marugoto.ch","wrongToken","NewPasswod1");
		String content = new ObjectMapper().writeValueAsString(passwodResetModel);

		mvc.perform(post("/api/user/password-reset")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
    	.andExpect(status().is(400));		
		
	}
}
