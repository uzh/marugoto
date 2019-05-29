package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.application.Gender;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.exception.TopicNotSelectedException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class StateControllerTest extends BaseControllerTest {

	@Autowired
	private ExerciseStateRepository exerciseStateRepository;

	@Test
	public void testGetStatesForCurrentPage() throws Exception {
		mvc.perform(authenticate(
				get("/api/states/")))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageTransitionStates", notNullValue()))
				.andExpect(jsonPath("$.pageComponents", notNullValue()));
	}

	@Test
	public void testGetStatesForCurrentPageExceptionIsThrownWhenStatesNotExist() throws Exception {
		user = new User(Gender.Male, "test", "tester", "tester@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		user.setCurrentPageState(null);
		userRepository.save(user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.exception", is(TopicNotSelectedException.class.getSimpleName())));
	}
	
	@Test
	public void test1UpdateExerciseStateWithCorrectSolution() throws Exception {
		PageState pageState = user.getCurrentPageState();
		var exerciseState = exerciseStateRepository.findByPageStateId(pageState.getId()).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseState.getId())
				.content("{\"inputState\": \"thank\" }"))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.statesChanged", notNullValue()))
			.andExpect(jsonPath("$.statesChanged").value(true));
	}

	@Test
	public void test1UpdateExerciseStateWithIncorrectSolution() throws Exception {
		PageState pageState = user.getCurrentPageState();
		var exerciseState = exerciseStateRepository.findByPageStateId(pageState.getId()).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseState.getId())
					.content("{\"inputState\": \"wrong\" }"))
					.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.statesChanged", notNullValue()))
				.andExpect(jsonPath("$.statesChanged").value(false));
	}
}
