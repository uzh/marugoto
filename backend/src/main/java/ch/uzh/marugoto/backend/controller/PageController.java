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

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.GameStateBrokenException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
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
	protected Messages messages;

	/**
	 * Loads last visited page for user
	 * If it's first time for user then it should start chosen topic
	 *
	 * @return
	 * @throws AuthenticationException
	 */
	@ApiOperation(value = "Load current page.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/current")
	public HashMap<String, Object> getPage() throws AuthenticationException, GameStateBrokenException {
		User authenticatedUser = getAuthenticatedUser();
		return stateService.getStates(authenticatedUser);
	}

	/**
	 * Page transition from page to page
	 * Everything that should happen before loading page and leaving previous one
	 *
	 * @param pageTransitionId
	 * @param chosenByPlayer
	 * @return
	 * @throws AuthenticationException
	 * @throws PageTransitionNotAllowedException
	 */
	@ApiOperation(value = "Handles a pagetransition from the current page to another page.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page updateStatesAfterTransition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException, PageTransitionNotAllowedException, GameStateBrokenException {
		User user = getAuthenticatedUser();
		TransitionChosenOptions chosenBy = TransitionChosenOptions.autoTransition;
		if(chosenByPlayer) {
			chosenBy = TransitionChosenOptions.player;
		}
		
		stateService.doPageTransition(chosenBy, "pageTransition/" + pageTransitionId, user);
		return stateService.getStates(user);
	}
}
