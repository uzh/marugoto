
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

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Manages game state.
 */
@RestController
public class StateController extends BaseController {

	@Autowired
	private PageService pageService;

	@Autowired
	private PageTransitionService pageTransitionService;

	@Autowired
	private ExerciseService exerciseService;


	@ApiOperation(value = "Returns all state objects", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("states")
	public Map<String, Object> getPageStates() throws Exception {
		PageState pageState = getAuthenticatedUser().getCurrentPageState();
		if (pageState == null) {
			throw new Exception("No existing states for the user");
		}
		return pageService.getAllStates(pageState.getPage(), getAuthenticatedUser());
	}

	@ApiOperation(value = "Updates exercise state in 'real time' and checks if exercise is correct", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "states/exerciseState/{exerciseStateId}", method = RequestMethod.PUT)
	public Map<String, Object> updateExerciseState(@ApiParam("ID of exercise state") @PathVariable String exerciseStateId,
			@ApiParam("Input state from exercise") @RequestParam("inputState") String inputState) throws AuthenticationException {
		ExerciseState exerciseState = exerciseService.updateExerciseState("exerciseState/" + exerciseStateId, inputState);
		boolean correct = exerciseService.isExerciseCorrect(exerciseState);
		pageTransitionService.updateTransitionAvailability(exerciseState);
		var objectMap = new HashMap<String, Object>();
		objectMap.put("exerciseCorrect", correct);
		objectMap.put("stateChanged", false);
		return objectMap;
	}
}
