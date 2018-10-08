package ch.uzh.marugoto.backend.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import ch.uzh.marugoto.backend.resource.AuthToken;
import ch.uzh.marugoto.backend.security.WebSecurityConfig;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.UserRepository;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseControllerTest extends BaseBackendTest {

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected UserRepository userRepository;

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
	private void createDefaultUser() {
		// Default user used to authenticate
		userRepository.save(new User(UserType.Guest, Salutation.Mr, "Unit", "Test", "defaultuser@marugoto.ch",
				securityConfig.encoder().encode("test")));
	}

	/**
	 * Retrieves authentication token and applies it to the given request builder.
	 */
	protected MockHttpServletRequestBuilder authenticate(MockHttpServletRequestBuilder builder) throws Exception {
		var resStr = mvc
			.perform(post("/api/auth/generate-token")
					.content("{\"mail\":\"defaultuser@marugoto.ch\",\"password\":\"test\"}")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(200))
			.andExpect(jsonPath("$.token", notNullValue()))
			.andReturn()
			.getResponse()
			.getContentAsString();

		var token = new ObjectMapper().readValue(resStr, AuthToken.class);
		builder = builder.header("Authorization", "Bearer " + token.getToken());
		
		return builder;
	}
}
