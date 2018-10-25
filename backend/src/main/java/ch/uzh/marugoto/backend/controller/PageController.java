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

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * API to get the page with pageTransitions.
 */
@RestController
public class PageController extends BaseController {

	@Autowired
	private PageStateService pageStateService;

	@Autowired
	private PageTransitionStateService pageTransitionStateService;

	@Autowired
	private ExerciseStateService exerciseStateService;

	@Autowired
	private ModuleRepository moduleRepository;

	@ApiOperation(value = "Load page by ID.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/current")
	public HashMap<String, Object> getPage() throws AuthenticationException {
		User user = getAuthenticatedUser();
		Page page = user.getCurrentPageState().getPage();

		if (page == null)
			page = moduleRepository.findAll().iterator().next().getPage();

		PageState pageState = pageStateService.getState(page, getAuthenticatedUser());

		HashMap<String, Object> response = new HashMap<>();
		response.put("pageState", pageState);
		response.put("exerciseState", exerciseStateService.getAllExerciseStates(pageState));
		response.put("storylineState", pageState.getStorylineState());

		return response;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page transition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException, PageTransitionNotAllowedException {
		Page nextPage = pageTransitionStateService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId, getAuthenticatedUser());
		PageState pageState = pageStateService.getState(nextPage, getAuthenticatedUser());

		HashMap<String, Object> response = new HashMap<>();
		response.put("pageState", pageState);
		response.put("exerciseState", exerciseStateService.getAllExerciseStates(pageState));
		response.put("storylineState", pageState.getStorylineState());
		return response;
	}
}
