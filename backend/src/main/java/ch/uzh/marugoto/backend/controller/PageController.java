package ch.uzh.marugoto.backend.controller;

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
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.PageService;
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
	private ModuleRepository moduleRepository;

	@ApiOperation(value = "Load page by ID.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/current")
	public Page getPage() throws AuthenticationException {
		User user = getAuthenticatedUser();
		Page currentPage = null;
		
		if (user.getCurrentPageState() != null) {
			currentPage = user.getCurrentPageState().getPage();	
		} else {
			var module = moduleRepository.findAll().iterator().next();
			currentPage = module.getPage();
		}
		return currentPage;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "pageTransitions/doPageTransition/pageTransition/{pageTransitionId}", method = RequestMethod.POST)
	public Map<String, Object> doPageTransition(@ApiParam("ID of page transition") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosenByPlayer") boolean chosenByPlayer) throws AuthenticationException, PageTransitionNotAllowedException {
		Page nextPage = pageService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId, getAuthenticatedUser());
		var response = pageService.getAllStates(nextPage, getAuthenticatedUser());
		response.put("page", nextPage);
		return response;
	}
}
