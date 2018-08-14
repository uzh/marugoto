package ch.uzh.marugoto.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Root index controller for the backend API.
 * 
 * @author Vitamin2 AG
 */
@RestController
public class IndexController extends BaseController {

	@GetMapping("/")
	public String index() {
		return "Marugoto backend running.";
	}
}
