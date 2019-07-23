package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import ch.uzh.marugoto.backend.test.BaseControllerTest;

@AutoConfigureMockMvc
public class AuthenticationControllerTest extends BaseControllerTest {
	
	/**
	 * Send a valid authentication request. Token should be returned.
	 */
	@Test
	public void validAuthTest() throws Exception {
		mvc.perform(post("/api/auth/generate-token")
				.content("{\"mail\":\"unittest@marugoto.ch\",\"password\":\"test\"}")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().is(200))
        	.andExpect(jsonPath("$.token", notNullValue()))
			.andReturn();
	}

	/**
	 * Send a valid shibboleth authentication request. Token should be returned.
	 */
	@Test
	public void validShibbolethAuthTest() throws Exception {
		mvc.perform(post("/api/auth/shibboleth")
				.content("{\"email\":\"donald@marugoto.ch\",\"commonName\":\"Donald Duck\"}")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.token", notNullValue()))
				.andReturn();
	}

	/**
	 * Send authentication request with invalid credentials,
	 * BadCredentialsException (status 400) should be returned.
	 * 
	 * @throws Exception
	 */
	@Test
	public void invalidAuthTest() throws Exception {
		mvc.perform(post("/api/auth/generate-token")
				.content("{\"mail\":\"INVALID\",\"password\":\"INVALID\"}")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().is(400))
        	.andExpect(jsonPath("$.message", is("Bad credentials")))
        	.andExpect(jsonPath("$.exception", is("BadCredentialsException")))
			.andReturn();
	}
	
	/**
	 * Validate an authenticated request, status 200 OK should be returned.
	 */
	@Test
	public void validate1Test() throws Exception {
		mvc.perform(authenticate(get("/api/auth/validate")))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.mail", notNullValue()))
        	.andExpect(jsonPath("$.firstName", notNullValue()))
        	.andExpect(jsonPath("$.lastName", notNullValue()))
			.andReturn();
	}

	/**
	 * Validate a request without a token, status 401 Unauthorized should be returned.
	 */
	@Test
	public void validate2Test() throws Exception {
		mvc.perform(get("/api/auth/validate"))
        	.andExpect(status().is(401)) // Client not authenticated.
			.andReturn();
	}

	/**
	 * Validate a request with an invalid token, status 401 Unauthorized should be returned.
	 */
	@Test
	public void validate3Test() throws Exception {
		mvc.perform(get("/api/auth/validate")
				.header("Authorization", "Bearer INVALID_TOKEN"))
        	.andExpect(status().is(401)) // Client not authenticated.
			.andReturn();
	}

	/**
	 * Validate a request with a invalid token, status 401Unauthorized should be returned.
	 */
	@Test
	public void validate4Test() throws Exception {
		mvc.perform(get("/api/auth/validate")
				.header("Authorization", "Bearer NOT.REALLY.VALID"))
        	.andExpect(status().is(401)) // Client not authenticated.
			.andReturn();
	}
}
