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

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.PageRepository;
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
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("defaultuser@marugoto.ch");
		stateService.getPageState(page, user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageState", notNullValue()))
				.andExpect(jsonPath("$.exerciseState", notNullValue()));
	}

	@Test
	public void test2GetPageStatesExceptionIsThrownWhenStatesNotExist() throws Exception {
		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfTextExerciseisCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 1");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageStateWithExercise = stateService.getPageState(page, user);

		var exerciseStates = stateService.getExercisesState(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "some input text")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(false));
	}
		
	@Test
	public void test1UpdateExerciseStateAndIfRadioButtonExerciseisCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageStateWithExercise = stateService.getPageState(page, user);

		var exerciseStates = stateService.getExercisesState(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "3")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(true));
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfDateExerciseisCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 3");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageStateWithExercise = stateService.getPageState(page, user);

		var exerciseStates = stateService.getExercisesState(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "2018-12-06 12:32")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(true));
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfCheckboxExerciseisCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 4");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var pageStateWithExercise = stateService.getPageState(page, user);

		var exerciseStates = stateService.getExercisesState(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "1,3,4")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(false));
	}
	
}
