
package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.RequestAction;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.UpdateExerciseState;
import ch.uzh.marugoto.core.exception.DateNotValidException;
import ch.uzh.marugoto.core.exception.GameStateBrokenException;
import ch.uzh.marugoto.core.security.ExerciseStateGate;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
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
	private PageTransitionStateService pageTransitionStateService;
	@Autowired
	private StateService stateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	protected Messages messages;
	@Autowired
    private ExerciseStateGate exerciseStateGate;

	@ApiOperation(value = "Returns all state objects", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("states")
	public Map<String, Object> getStatesForCurrentPage() throws AuthenticationException, GameStateBrokenException {
		User authenticatedUser = getAuthenticatedUser();
		return stateService.getStates(authenticatedUser);
	}

	@ApiOperation(value = "Updates exercise state in 'real time' and checks if exercise is correct", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "states/exerciseState/{exerciseStateId}", method = RequestMethod.PUT)
	public Map<String, Object> updateExerciseState(@ApiParam("ID of exercise state") @PathVariable String exerciseStateId,
			@ApiParam("Input state from exercise") @RequestBody(required = false) UpdateExerciseState exerciseState) throws AuthenticationException, DateNotValidException {
		User user = getAuthenticatedUser();
		exerciseStateId = "exerciseState/" + exerciseStateId;

		isUserAuthorized(RequestAction.UPDATE, user, exerciseStateGate, exerciseStateService.getExerciseState(exerciseStateId));

		exerciseStateService.updateExerciseState(exerciseStateId, exerciseState.getInputState());
		boolean statesChanged = pageTransitionStateService.checkPageTransitionStatesAvailability(getAuthenticatedUser());
		var objectMap = new HashMap<String, Object>();
		objectMap.put("statesChanged", statesChanged);
		return objectMap;
	}
}
