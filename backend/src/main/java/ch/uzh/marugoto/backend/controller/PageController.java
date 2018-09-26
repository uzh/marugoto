package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * API to get the page with pageTransitions.
 */
@RestController
public class PageController extends BaseController {
	@Autowired
	private PageService pageService;

	@Autowired
	private StateService stateService;

	@ApiOperation(value = "Load page by ID.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/page/{id}")
	public Map<String, Object> getPage(@ApiParam("ID of page") @PathVariable String id) throws AuthenticationException {
		Page page = pageService.getPage("page/" + id);
		StorylineState storylineState = stateService.getStorylineState(getAuthenticatedUser(), page);
		// possible for first info page and we need page state even if storyline is not started, right?
		// how if we don't have storyline state at this moment ??? (probably user reference should be added to page state)
		PageState pageState = null;
		if (storylineState == null) {
			// create page state ??
//			PageState pageState = stateService.getPageState(page, storylineState);
		} else {
			pageState = storylineState.getCurrentlyAt();
		}

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", page);
		objectMap.put("storylineState", storylineState);
		objectMap.put("pageState", pageState);

		if (pageState != null) {
			List<ExerciseState> exerciseStates = stateService.getExerciseStates(pageState);
			objectMap.put("exerciseState", exerciseStates);
		}


		return objectMap;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page transition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException {
		Page nextPage = pageService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId, getAuthenticatedUser());
		StorylineState storylineState = getAuthenticatedUser().getCurrentlyPlaying();
		List<ExerciseState> nextPageExerciseStates = stateService.getExerciseStates(storylineState.getCurrentlyAt());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", nextPage);
		objectMap.put("storylineState", storylineState);
		objectMap.put("pageState", storylineState.getCurrentlyAt());
		objectMap.put("exerciseState", nextPageExerciseStates);
		return objectMap;
	}
}
