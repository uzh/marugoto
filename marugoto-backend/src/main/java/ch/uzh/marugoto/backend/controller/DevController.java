package ch.uzh.marugoto.backend.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller only used for development purposes, e.g. testing exception responses, JSON formatting etc.
 */
@RestController
@RequestMapping("dev")
public class DevController {

	@GetMapping("throwException")
	public void throwException() throws Exception {
		throw new Exception("Exception message.");
	}

	@GetMapping("throwWithInnerException")
	public void throwWithInnerException() throws Exception {
		throw new Exception("Exception message.", new IllegalStateException("Inner exception message."));
	}

	@GetMapping("date")
	public Object returnDate() {
		return new Date();
	}
}
