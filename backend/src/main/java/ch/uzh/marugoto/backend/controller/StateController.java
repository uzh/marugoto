
package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.service.ComponentService;
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
	
	@Autowired
	private ComponentService componentService;

	@ApiOperation(value = "Updates exercise state in 'real time' and checks if exercise is correct", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "states/exerciseState/{exerciseStateId}", method = RequestMethod.PUT)
	public Map<String, Object> updateExerciseState(@ApiParam("ID of exercise state.") @PathVariable String exerciseStateId,
			@ApiParam("Input text from exercise") @RequestParam("input_text") String inputText)
			throws AuthenticationException {
		ExerciseState exerciseState = stateService.updateExerciseState("exerciseState/" + exerciseStateId, inputText);
		boolean correct = componentService.isExerciseCorrect(exerciseState);
		var objectMap = new HashMap<String, Object>();
		objectMap.put("exerciseCorrect", correct);
		return objectMap;
	}
}
