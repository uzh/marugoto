package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.backend.data.DbConfiguration;
import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.entity.UserType;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.security.WebSecurityConfig;
import ch.uzh.marugoto.backend.test.BaseTest;

@AutoConfigureMockMvc
public class AuthenticationControllerTest extends BaseTest {
	
	private boolean onlyOnce;
	
	
    @Autowired
    private MockMvc mvc;

	@Autowired
	private ArangoOperations operations;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DbConfiguration _dbConfig;

	@Autowired
	private WebSecurityConfig _securityConfig;
	
	
    @Before
    public void setup() {
    	// TODO Rino: Use new base class method (not commited yet)
    	if (onlyOnce)
    		return;
    	
    	onlyOnce = true;
    	
    	// Clear database
		operations.dropDatabase();
		operations.driver().createDatabase(_dbConfig.database());
    	
		Log.info("Database truncated");
		
		// Create user
		userRepository.save(new User(UserType.Guest, Salutation.Mr, "Hans", "Muster", "hm", _securityConfig.encoder().encode("test")));
    }

	@Test
	public void validAuthTest() throws Exception {
		mvc.perform(post("/api/auth/generate-token")
				.content("{\"username\":\"hm\",\"password\":\"test\"}")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().is(200))
        	.andExpect(jsonPath("$.token", notNullValue()))
			.andReturn();
	}

	@Test
	public void invalidAuthTest() throws Exception {
		mvc.perform(post("/api/auth/generate-token")
				.content("{\"username\":\"INVALID\",\"password\":\"INVALID\"}")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().is(400))
        	.andExpect(jsonPath("$.message", is("Bad credentials")))
        	.andExpect(jsonPath("$.exception", is("BadCredentialsException")))
			.andReturn();
	}
}
