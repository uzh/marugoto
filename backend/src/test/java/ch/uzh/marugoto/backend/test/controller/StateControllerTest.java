package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StateService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class StateControllerTest extends BaseControllerTest {
		
	@Autowired
	private StateService stateService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PageRepository pageRepository;

	@Test
	public void test1GetPageStates() throws Exception {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("defaultuser@marugoto.ch");
		var statesInitialized = stateService.getPageState(page, user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageState", notNullValue()));
	}

	@Test
	public void test2GetPageStatesExeptionIsThrownWhenStatesNotExist() throws Exception {
		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void test1UpdateExerciseState() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("defaultuser@marugoto.ch");
		var pageStateWithExercise = stateService.getPageState(page, user);

		var exerciseStates = stateService.getExerciseStates(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "Some input text for exercise")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(false));
	}
}
