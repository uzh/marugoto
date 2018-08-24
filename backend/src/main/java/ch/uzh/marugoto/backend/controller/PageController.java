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
		PageState pageState = this.stateService.getPageState(page, getAuthenticatedUser());
		List<PageTransition> pageTransitions = this.pageService.getPageTransitions("page/" + id);

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", page);
		objectMap.put("pageTransitions", pageTransitions);
		objectMap.put("pageState", pageState);
		return objectMap;
	}

	@ApiOperation(value = "Triggers page transition and state updates.", authorizations = {
			@Authorization(value = "apiKey") })
	@GetMapping("pages/pageTransition/{pageTransitionId}")
	public Map<String, Object> doPageTransition(
			@ApiParam("ID of page transition.") @RequestParam String pageTransitionId) throws AuthenticationException {

		Page nextPage = pageService.doTransition("pageTransition/" + pageTransitionId, getAuthenticatedUser());
		PageState nextPageState = this.stateService.getPageState(nextPage, getAuthenticatedUser());
		List<PageTransition> nextPageTransitions = this.pageService.getPageTransitions(nextPage.getId());

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", nextPage);
		objectMap.put("pageState", nextPageState);
		objectMap.put("pageTransitions", nextPageTransitions);
		
		return null;
	}
}
