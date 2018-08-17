package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageTransition;
import ch.uzh.marugoto.backend.service.PageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Backend API to get the page with pageTransitions.
 */
@RestController
@RequestMapping("pages")
public class PageController extends BaseController {

	@Autowired
	private PageService pageService;
	
	@ApiOperation("Load all available pages.")
	@GetMapping("list")
	public Iterable<Page> getPages() {
		Iterable<Page> pages = this.pageService.getAllPages();
		return pages;
	}

	@ApiOperation("Load page by ID.")
	@GetMapping("{id}")
	public Map<String, Object> getPage(@ApiParam("ID of page.") @PathVariable String id) {
		Page page = this.pageService.getPage("page/" + id);
		List<PageTransition> pageTransitions = this.pageService.getPageTransitions("page/" + id);
		Map<String, Object> objectMap = new HashMap<String, Object>();
		objectMap.put("page", page);
		objectMap.put("pageTransitions", pageTransitions);
		return objectMap;
	}
}
