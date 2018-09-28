package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

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

	private PageState pageStateWithExercise;
	
	@Override
	protected void setupOnce () {
		super.setupOnce();
		var page = pageRepository.findByTitle("Page 2");
		var user = userRepository.findByMail("unittest@marugoto.ch");
		pageStateWithExercise = stateService.getPageState(page, user);
	}
	
	@Test
	public void test1UpdateExerciseState() throws Exception {
		var exerciseStates = stateService.getExerciseStates(pageStateWithExercise).get(0);
		mvc.perform(authenticate(
				put("/api/states/" + exerciseStates.getId())
				.param("inputState", "Some input text for exercise")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exerciseCorrect", notNullValue()))
			.andExpect(jsonPath("$.exerciseCorrect").value(false));
	}
}
