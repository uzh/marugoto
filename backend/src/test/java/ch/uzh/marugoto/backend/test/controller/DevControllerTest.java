package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.text.SimpleDateFormat;

import ch.uzh.marugoto.backend.test.BaseControllerTest;

@AutoConfigureMockMvc
public class DevControllerTest extends BaseControllerTest {

	@Test
	public void throwExceptionTest() throws Exception {
		mvc.perform(get("/api/dev/throwException"))
        	.andExpect(status().is(400))
        	.andExpect(jsonPath("$.message", is("Exception message.")))
        	.andExpect(jsonPath("$.path", is("/api/dev/throwException")))
        	.andExpect(jsonPath("$.exception", is("Exception")))
        	.andExpect(jsonPath("$.file", containsString("DevController.java")))
        	.andExpect(jsonPath("$.stackTrace", notNullValue()))
			.andReturn();
	}

	@Test
	public void throwWithInnerExceptionTest() throws Exception {
		mvc.perform(get("/api/dev/throwWithInnerException"))
        	.andExpect(status().is(400))
        	.andExpect(jsonPath("$.message", is("Exception message.")))
        	.andExpect(jsonPath("$.path", is("/api/dev/throwWithInnerException")))
        	.andExpect(jsonPath("$.exception", is("Exception")))
        	.andExpect(jsonPath("$.file", containsString("DevController.java")))
        	.andExpect(jsonPath("$.stackTrace", notNullValue()))
        	.andExpect(jsonPath("$.innerException", notNullValue()))
        	.andExpect(jsonPath("$.innerException.message", is("Inner exception message.")))
        	.andExpect(jsonPath("$.innerException.exception", is("IllegalStateException")))
			.andReturn();
	}

	@Test
	public void returnDateTest() throws Exception {
		mvc.perform(get("/api/dev/date"))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.date", is(new SimpleDateFormat("yyyy/MM/dd").parse("2000/01/01").toString())))
			.andReturn();
	}
}
