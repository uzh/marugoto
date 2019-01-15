package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.ComponentService;
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
	private StateService stateService;
	@Autowired
	private ComponentService componentService;

	@ApiOperation(value = "Load current page.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/current")
	public HashMap<String, Object> getPage() throws AuthenticationException {
		User authenticatedUser = getAuthenticatedUser();
		
		//open first page from topic, if there is no pageState
		if (authenticatedUser.getCurrentPageState() == null) {
			stateService.startTopic(authenticatedUser);
        }
		
		var response = stateService.getStates(authenticatedUser);
		Page page = authenticatedUser.getCurrentPageState().getPage();
		page.setComponents(componentService.getPageComponents(page));
		response.put("page", page);
		return response;
	}

	@ApiOperation(value = "Handles a pagetransition from the current page to another page.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page updateStatesAfterTransition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException, PageTransitionNotAllowedException {
		User user = getAuthenticatedUser();
		TransitionChosenOptions chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
		Page nextPage = stateService.doPageTransition(chosenBy, "pageTransition/" + pageTransitionId, user);
		
		var response = stateService.getStates(user);
		nextPage.setComponents(componentService.getPageComponents(nextPage));
		response.put("page", nextPage);
		return response;
	}
}
