package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.service.PageService;

@RestController
public class PageController extends BaseController {

	@Autowired
	private PageService pageService;
	
	
	@GetMapping("/pages/list")
	public Iterable<Page> getPages() {
		Iterable<Page> pages = this.pageService.getAllPages();
		return pages;
	}
	
	@GetMapping("/pages/{id}")
	public Page getPage(@PathVariable String id) {
		Page page = this.pageService.getPage(id);
		return page;
	}
}
