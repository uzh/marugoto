package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
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
		var response = stateService.getAllStates(page, getAuthenticatedUser());
		response.put("page", page);
		return response;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page transition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException, PageTransitionNotAllowedException {
		Page nextPage = pageService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId, getAuthenticatedUser());
		var response = stateService.getAllStates(nextPage, getAuthenticatedUser());
		response.put("page", nextPage);
		return response;
	}
}
