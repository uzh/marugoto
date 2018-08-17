package ch.uzh.marugoto.backend.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;

/**
 * Dummy test case to call IndexController over HTTP.
 * 
 */
@AutoConfigureMockMvc
public class IndexControllerTest extends BaseControllerTest {

	@Test
	public void indexTest() throws Exception {
		mvc.perform(get("/"))
        	.andExpect(status().isOk())
        	.andExpect(content().string("Marugoto backend service running."))
			.andReturn();
	}

	@Test
	public void apiIndexTest() throws Exception {
		mvc.perform(get("/api/"))
        	.andExpect(status().isOk())
        	.andExpect(content().string("Marugoto backend API service running."))
			.andReturn();
	}
}
