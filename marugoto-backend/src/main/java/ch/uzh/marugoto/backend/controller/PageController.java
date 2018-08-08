package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.service.PageService;

@RestController
public class PageController extends BaseController {

	@Autowired
	private PageService pageService;

	@RequestMapping("/getPages")
	public String getPages() {
		Iterable<Page> pages = this.pageService.getAllPages();
		String json = "[";
		Gson gson = new Gson();
		for (Page page : pages) {
			json = json + gson.toJson(page) + ",";
		}
		json = json + "]";
		return json;
	}
}
