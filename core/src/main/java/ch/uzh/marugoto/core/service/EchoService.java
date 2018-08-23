package ch.uzh.marugoto.core.service;

import org.springframework.stereotype.Service;

/**
 * Service to test core-project, will be removed in the future.
 */
@Service
public class EchoService {

	public String echo(String input) {
		return "You provided the following input: " + input;
	}
}
