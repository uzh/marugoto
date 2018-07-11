package ch.uzh.marugoto.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController extends BaseController {

	@RequestMapping("/")
	public String index() {
		return "Marugoto backend running.";
	}
}
