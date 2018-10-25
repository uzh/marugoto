
package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Manages game state.
 */
@RestController
public class StateController extends BaseController {

	@Autowired
	private PageTransitionStateService pageTransitionStateService;

	@Autowired
	private ExerciseStateService exerciseStateService;


	@ApiOperation(value = "Returns all state objects", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("states")
	public Map<String, Object> getPageStates() throws Exception {
		PageState pageState = getAuthenticatedUser().getCurrentPageState();

		if (pageState == null)
			throw new PageStateNotFoundException();

		HashMap<String, Object> response = new HashMap<>();
		response.put("pageState", pageState);
		response.put("exerciseState", exerciseStateService.getAllExerciseStates(pageState));
		response.put("storylineState", pageState.getStorylineState());

		return response;
	}

	@ApiOperation(value = "Updates exercise state in 'real time' and checks if exercise is correct", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "states/exerciseState/{exerciseStateId}", method = RequestMethod.PUT)
	public Map<String, Object> updateExerciseState(@ApiParam("ID of exercise state") @PathVariable String exerciseStateId,
			@ApiParam("Input state from exercise") @RequestParam("inputState") String inputState) throws PageTransitionNotFoundException {
		ExerciseState exerciseState = exerciseStateService.updateExerciseState("exerciseState/" + exerciseStateId, inputState);
		boolean stateChanged = pageTransitionStateService.updateTransitionAvailability(exerciseState);
		var objectMap = new HashMap<String, Object>();
		objectMap.put("stateChanged", stateChanged);
		return objectMap;
	}
}
