
package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Manages game state.
 */
@RestController
public class StateController extends BaseController {
	
	@Autowired
	private StateService stateService;


	@ApiOperation(value = "Load state of page for authenticated user.", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("pages/{id}/state")
	public PageState getPageState(@ApiParam("ID of page.") @PathVariable String id) {
		PageState pageState = this.stateService.getPageState("page/" + id);
		return pageState;
	}
}
