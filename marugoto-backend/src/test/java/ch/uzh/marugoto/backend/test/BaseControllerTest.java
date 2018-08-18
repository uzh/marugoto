package ch.uzh.marugoto.backend.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.entity.UserType;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.resource.AuthToken;
import ch.uzh.marugoto.backend.security.WebSecurityConfig;

public abstract class BaseControllerTest extends BaseTest {

	@Autowired
	protected MockMvc mvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WebSecurityConfig securityConfig;

	@Override
	protected void setupOnce() {
		super.setupOnce();

		createDefaultUser();
	}

	/**
	 * Creates default user used for authentication.
	 */
	protected void createDefaultUser() {
		// Default user used to authenticate
		userRepository.save(new User(UserType.Guest, Salutation.Mr, "Unit", "Test", "test",
				securityConfig.encoder().encode("test")));
	}

	/**
	 * Retrieves authentication token and applies it to the given request builder.
	 */
	protected MockHttpServletRequestBuilder authenticate(MockHttpServletRequestBuilder builder) throws Exception {
		var resStr = mvc
			.perform(post("/api/auth/generate-token")
					.content("{\"username\":\"test\",\"password\":\"test\"}")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(200))
			.andExpect(jsonPath("$.token", notNullValue()))
			.andReturn()
			.getResponse()
			.getContentAsString();

		var mapper = new ObjectMapper();
		var token = mapper.readValue(resStr, AuthToken.class);
		
		builder = builder.header("Authorization", "Bearer " + token.getToken());
		
		return builder;
	}
}
