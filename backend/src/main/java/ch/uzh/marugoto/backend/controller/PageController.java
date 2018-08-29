package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Backend API to get the page with pageTransitions.
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
		Page page = this.pageService.getPage("page/" + id);
		PageState pageState = this.stateService.initPageStates(page, getAuthenticatedUser());
		List<PageTransition> pageTransitions = this.pageService.getPageTransitions(page.getId());
		List<PageTransitionState> pageTransitionStates = this.stateService.initPageTransitionStates(pageTransitions,
				getAuthenticatedUser());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("pageState", pageState);
		objectMap.put("pageTransitionStates", pageTransitionStates);
		return objectMap;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = {
			@Authorization(value = "apiKey") })
	@GetMapping("pages/pageTransition/{pageTransitionId}")
	public Map<String, Object> doPageTransition(
			@ApiParam("ID of page transition.") @PathVariable String pageTransitionId,
			@ApiParam("Is chosen by player ") @RequestParam("chosen_by_player") boolean chosenByPlayer)
			throws AuthenticationException {

		Page nextPage = pageService.doTransition(chosenByPlayer, "pageTransition/" + pageTransitionId,
				getAuthenticatedUser());
		PageState nextPageState = this.stateService.initPageStates(nextPage, getAuthenticatedUser());
		List<PageTransition> nextPageTransitions = this.pageService.getPageTransitions(nextPage.getId());
		List<PageTransitionState> nextPageTransitionStates = this.stateService
				.initPageTransitionStates(nextPageTransitions, getAuthenticatedUser());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("pageState", nextPageState);
		objectMap.put("pageTransitionStates", nextPageTransitionStates);
		return objectMap;
	}
}
