package ch.uzh.marugoto.backend.test.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Dummy test case to call IndexController over HTTP.
 * 
 */
@AutoConfigureMockMvc
public class IndexControllerTest extends BaseTest {
	
    @Autowired
    private MockMvc mvc;
    
    
	@Test
	public void indexTest() throws Exception {
		var res = mvc.perform(get("/"))
        	.andExpect(status().isOk())
			.andReturn();

		var resStr = res.getResponse().getContentAsString();
		
		Log.info(resStr);
		
		assertEquals(resStr, "Marugoto backend running.");
	}
}
