package ch.uzh.marugoto.backend.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ch.uzh.marugoto.backend.resource.RegisterUser;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Salutation;

@AutoConfigureMockMvc
public class RegistrationControllerTest extends BaseControllerTest{
	
	@Test
	@JsonSerialize
	public void testRegister() throws Exception{
		
		RegisterUser user = new RegisterUser(Salutation.Mr,"Fredi","Kruger","fredi@maal.com","Pasword1");
		String content = new ObjectMapper().writeValueAsString(user);

		mvc.perform(authenticate(post("/api/user/registration")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8)))
    	.andExpect(status().is(200));		
	}
	
}
