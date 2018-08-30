
package ch.uzh.marugoto.backend.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Manages game state.
 */
@RestController
public class StateController extends BaseController {

	@Autowired
	private StateService stateService;

	@ApiOperation(value = "Updates exercise state in 'real time' and checks exercise solution", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "states/pageState/{pageStateId}/exerciseState/{exerciseStateId}", method = RequestMethod.PUT)
	public ExerciseState updateExerciseState(@ApiParam("ID of page state.") @PathVariable String pageStateId,
			@ApiParam("ID of exercise state.") @PathVariable String exerciseStateId,
			@ApiParam("Input text from exercise") @RequestParam("input_text") String inputText)
			throws AuthenticationException {
		PageState pageState = stateService.getPageState(pageStateId);
		ExerciseState exerciseState = stateService.updadeExerciseState("pageState/" + pageStateId,
				"textExercise/" + exerciseStateId, inputText);
//		boolean solved = pageService.checkTextExercise(exerciseState, getAuthenticatedUser());
		return null;
	}
}
