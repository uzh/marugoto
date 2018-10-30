package ch.uzh.marugoto.backend.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.util.UUID;

import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.repository.UserRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
		mvc.perform(post("/api/user/password-forget")
				.param("mail", "unittest@marugoto.ch")
				.param("passwordResetUrl", "/api/user/password-reset"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resetToken", notNullValue()));
	}
	
	@Test
	public void testForgotPasswordIfMailIsWrong() throws Exception {
		mvc.perform(post("/api/user/password-forget")
				.param("mail", "dada@marugoto.ch")
				.param("passwordResetUrl", "/api/user/password-reset"))
			.andDo(print())
        	.andExpect(status().is(400))
			.andExpect(jsonPath("$.message", is(messages.get("userNotFound.forEmail"))));
	}
	
	@Test
	public void testResetPassword() throws Exception{
		var user = userRepository.findByMail("unittest@marugoto.ch");
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		var password = "NewPassword1";
		mvc.perform(post("/api/user/password-reset")
				.param("mail", "unittest@marugoto.ch")
				.param("newPassword", password)
				.param("token", user.getResetToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testResetPasswordIfPasswordIsWrong() throws Exception{
		var user = userRepository.findByMail("unittest@marugoto.ch");
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		var password = "wrongpassword";
		mvc.perform(post("/api/user/password-reset")
				.param("mail", "unittest@marugoto.com")
				.param("newPassword", password)
				.param("token", user.getResetToken()))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.message", notNullValue()));
	}
	
	@Test
	public void testResetPasswordIfTokenIsWrong() throws Exception{
		var user = userRepository.findByMail("unittest@marugoto.ch");
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		mvc.perform(post("/api/user/password-reset")
				.param("mail", "unittest@marugoto.com")
				.param("newPassword", "NewPassword1")
				.param("token", "wrong_token_34234"))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.message", is(messages.get("userNotFound.forResetToken"))));
	}
	
	
	@Test
	public void testResetPasswordIfTokenAndEmailAreNotMatch() throws Exception{
		var user = userRepository.findByMail("unittest@marugoto.ch");
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		mvc.perform(post("/api/user/password-reset")
				.param("mail", "wrongemail@marugoto.ch")
				.param("newPassword", "NewPassword1")
				.param("token", user.getResetToken()))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.message", is("This is invalid password reset token")));
	}
}
