package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
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
	public Map<String, Object> getPage(@ApiParam("ID of page.") @PathVariable String id)
			throws AuthenticationException {
		Page page = pageService.getPage("page/" + id);
		PageState pageState = stateService.getPageState(page, getAuthenticatedUser());
		List<PageTransitionState> pageTransitionStates = stateService.getPageTransitionStates(page,
				getAuthenticatedUser());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", page);
		objectMap.put("pageState", pageState);
		objectMap.put("pageTransitionStates", pageTransitionStates);
		return objectMap;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(
			@ApiParam("ID of page transition.") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosen_by_player") boolean chosenByPlayer)
			throws AuthenticationException {

		Page nextPage = pageService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId,
				getAuthenticatedUser());
		PageState nextPageState = stateService.getPageState(nextPage, getAuthenticatedUser());
		List<PageTransitionState> nextPageTransitionStates = stateService.getPageTransitionStates(nextPage,
				getAuthenticatedUser());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", nextPage);
		objectMap.put("pageState", nextPageState);
		objectMap.put("pageTransitionStates", nextPageTransitionStates);
		return objectMap;
	}

	@ApiOperation(value = "Check exercise result - compares it with solutions.", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "pages/page/{pageId}/exercise/{exerciseId}/check", method = RequestMethod.POST)
	public boolean checkTextExercise(@ApiParam("ID of page.") @PathVariable String pageId,
			@ApiParam("ID of exercise.") @PathVariable String exerciseId,
			@ApiParam("Input text from exercise") @RequestParam("input_text") String inputText) throws AuthenticationException {
		PageState pageState = this.stateService.getPageState(this.pageService.getPage("page/" + pageId), getAuthenticatedUser());
		ExerciseState exerciseState = this.stateService.updadeExerciseState(pageState, exerciseId, inputText);
		boolean solved = this.pageService.checkTextExercise(exerciseState, getAuthenticatedUser());
		return solved;
	}
}
