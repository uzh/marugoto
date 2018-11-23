package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.service.ExerciseStateService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class StateControllerTest extends BaseControllerTest {

	@Autowired
	private ExerciseStateService exerciseStateService;

	@Before
	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
	}

	@Test
	public void testGetStatesForCurrentPage() throws Exception {
		mvc.perform(authenticate(
				get("/api/states/")))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pageTransitionStates", notNullValue()))
				.andExpect(jsonPath("$.exerciseStates", notNullValue()));
	}

	@Test
	public void testGetStatesForCurrentPageExceptionIsThrownWhenStatesNotExist() throws Exception {
		user = new User(UserType.Guest, Salutation.Mr, "test", "tester", "tester@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		user.setCurrentPageState(null);
		userRepository.save(user);

		mvc.perform(authenticate(
				get("/api/states/")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void test1UpdateExerciseStateAndIfTextExerciseIsCorrect() throws Exception {
		PageState pageState = user.getCurrentPageState();
		var exerciseState = exerciseStateService.getAllExerciseStates(pageState).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseState.getId())
				.param("inputState", "some text")))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.statesChanged", notNullValue()))
			.andExpect(jsonPath("$.statesChanged").value(true));
	}
}
