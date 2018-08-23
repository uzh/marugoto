package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
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
	public Map<String, Object> getPage(@ApiParam("ID of page.") @PathVariable String id) throws AuthenticationException {
		Page page = this.pageService.getPage("page/" + id);
		User user = getAuthenticatedUser();
		PageState pageState = this.stateService.getPageState(page, user);
		List<PageTransition> pageTransitions = this.pageService.getPageTransitions(id);

		var objectMap = new HashMap<String, Object>();
		objectMap.put("page", page);
		objectMap.put("pageTransitions", pageTransitions);
		objectMap.put("pageState", pageState);

		return objectMap;
	}
}
