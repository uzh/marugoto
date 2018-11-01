package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.StateService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class StateControllerTest extends BaseControllerTest {

	@Autowired
	private StateService pageStateService;

	@Autowired
	private ExerciseStateService exerciseStateService;

	@Autowired
	private PageRepository pageRepository;

	@Before
	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
	}

	@Test
	public void test1GetPageStates() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
		//pageStateService.getState(page, user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageState", notNullValue()))
				.andExpect(jsonPath("$.exerciseState", notNullValue()));
	}

	@Test
	public void test2GetPageStatesExceptionIsThrownWhenStatesNotExist() throws Exception {
		user = new User(UserType.Guest, Salutation.Mr, "test", "tester", "tester@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		user.setCurrentPageState(null);
		userRepository.save(user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfTextExerciseIsCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 1");
//		var pageStateWithExercise = pageStateService.getState(page, user);
//
//		var exerciseStates = exerciseStateService.getAllExerciseStates(pageStateWithExercise).get(0);
//		mvc.perform(authenticate(
//				put("/api/states/" + exerciseStates.getId())
//				.param("inputState", "some input text")))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.stateChanged", notNullValue()))
//			.andExpect(jsonPath("$.stateChanged").value(true));
	}
		
	@Test
	public void test1UpdateExerciseStateAndICheckboxExerciseIsIncorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
//		var pageStateWithExercise = pageStateService.getState(page, user);
//        var checkboxExerciseState = exerciseStateService.getAllExerciseStates(pageStateWithExercise)
//				.stream()
//				.filter(state -> state.getExercise() instanceof CheckboxExercise)
//				.findFirst().orElseThrow();
//
//		var exerciseState = exerciseStateService.getExerciseState(checkboxExerciseState.getExercise(), pageStateWithExercise);
//		mvc.perform(authenticate(
//				put("/api/states/" + exerciseState.getId())
//				.param("inputState", "3,4")))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.stateChanged", notNullValue()))
//			.andExpect(jsonPath("$.stateChanged").value(false));
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfDateExerciseIsCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 4");
//		var pageStateWithExercise = pageStateService.getState(page, user);
//
//		var dateExerciseState = exerciseStateService.getAllExerciseStates(pageStateWithExercise)
//				.stream()
//				.filter(state -> state.getExercise() instanceof DateExercise)
//				.findFirst().orElseThrow();
//
//		var exerciseState = exerciseStateService.getExerciseState(dateExerciseState.getExercise(), pageStateWithExercise);
//		mvc.perform(authenticate(
//				put("/api/states/" + exerciseState.getId())
//				.param("inputState", "2018-12-06 12:32")))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.stateChanged", notNullValue()))
//			.andExpect(jsonPath("$.stateChanged").value(false));
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfCheckboxExerciseIsCorrect() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
//		var pageStateWithExercise = pageStateService.getState(page, user);
//
//		var exerciseStates = exerciseStateService.getAllExerciseStates(pageStateWithExercise).get(0);
//		mvc.perform(authenticate(
//				put("/api/states/" + exerciseStates.getId())
//				.param("inputState", "1,3,4")))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.stateChanged", notNullValue()))
//			.andExpect(jsonPath("$.stateChanged").value(true));
	}
	
}
