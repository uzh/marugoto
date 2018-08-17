package ch.uzh.marugoto.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy root index controller for backend API.
 */
@RestController
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "Marugoto backend service running.";
	}

	@GetMapping("/api/")
	public String apiIndex() {
		return "Marugoto backend API service running.";
	}
}
