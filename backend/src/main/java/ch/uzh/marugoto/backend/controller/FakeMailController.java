package ch.uzh.marugoto.backend.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.MailExercise;
import ch.uzh.marugoto.core.service.FakeEmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class FakeMailController extends BaseController {

	@Autowired
	private FakeEmailService fakeEmailService;
	
	@ApiOperation(value = "List all emails where player walked through", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mails/list")
	public List<MailExercise>getAllEmails() throws AuthenticationException {
		return fakeEmailService.getAllMailExercises(getAuthenticatedUser().getId());
	}
	
	@ApiOperation(value = "Load email by its id", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mails/{id}")
	public Exercise getEmailById(@PathVariable String exerciseId) throws AuthenticationException {
		return fakeEmailService.getMailExerciseById(getAuthenticatedUser().getCurrentPageState().getId(), exerciseId);
	}
	@ApiOperation (value ="Send email", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mails/send/{mailExerciseId}", method = RequestMethod.PUT)
	public void sendEmail(@ApiParam("ID of exercise state") @PathVariable String mailExerciseId) throws AuthenticationException {
		fakeEmailService.sendEmail(getAuthenticatedUser().getCurrentPageState().getId(), mailExerciseId);
	}
}
