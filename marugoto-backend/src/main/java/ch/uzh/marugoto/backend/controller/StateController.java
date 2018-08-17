
package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.marugoto.backend.data.entity.PageState;
import ch.uzh.marugoto.backend.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * Controller responsible for states of the application
 *
 */

@RestController
public class StateController extends BaseController {
	
	@Autowired
	private StateService stateService;

	
	@ApiOperation("Get the page state")
	@GetMapping("pages/{id}/state")
	public PageState getPageState(@ApiParam("ID of page.") @PathVariable String id) {
		PageState pageState = this.stateService.getPageState("page/" + id);
		return pageState;
	}
}
