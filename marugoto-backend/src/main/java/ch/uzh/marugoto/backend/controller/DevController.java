package ch.uzh.marugoto.backend.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller only used for development purposes, e.g. testing exception responses, JSON formatting etc.
 */
@RestController
public class DevController extends BaseController {

	@GetMapping("dev/throwException")
	public void throwException() throws Exception {
		throw new Exception("Exception message.");
	}

	@GetMapping("dev/throwWithInnerException")
	public void throwWithInnerException() throws Exception {
		throw new Exception("Exception message.", new IllegalStateException("Inner exception message."));
	}

	@GetMapping("dev/date")
	public Map<String, Object> returnDate() throws ParseException {
		var result = new HashMap<String, Object>();
		result.put("date", new SimpleDateFormat("yyyy/MM/dd").parse("2000/01/01"));
	    return result;
	}
}
