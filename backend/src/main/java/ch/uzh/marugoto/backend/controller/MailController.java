package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class MailController extends BaseController {

	@Autowired
	private MailService mailService;
	@Autowired
	private StateService stateService;

	@ApiOperation(value = "List all emails where player walked through", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mail/list")
	public List<MailState>getAllMails() throws AuthenticationException {
		return mailService.getReceivedMails(getAuthenticatedUser());
	}

	@ApiOperation (value ="Mail received or mail has been read", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/sync/notification/{mailId}", method = RequestMethod.PUT)
	public void updateMail(@ApiParam("ID of mail") @PathVariable String mailId, @ApiParam("Mail has been read") @RequestParam boolean isRead) throws AuthenticationException {
		mailService.updateMailState("notification/" + mailId, getAuthenticatedUser(), isRead);
	}
	
	@ApiOperation (value ="Send mail reply", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/reply/notification/{mailId}", method = RequestMethod.PUT)
	public HashMap<String, Object> replyMail(@ApiParam("ID of mail exercise") @PathVariable String mailId, @ApiParam ("Mail reply text") @RequestParam String replyText) throws AuthenticationException, PageTransitionNotAllowedException {
		var user = getAuthenticatedUser();
		var response = new HashMap<String, Object>();

		mailService.replyOnMail(user, "notification/" + mailId, replyText);
		PageTransition pageTransition = mailService.getMailReplyTransition("notification/" + mailId, user.getCurrentPageState());

		if (pageTransition != null) {
			stateService.doPageTransition(TransitionChosenOptions.player, pageTransition.getId(), user);
			response.put("stateChanged", true);
		} else {
			response.put("stateChanged", false);
		}

		return response;
	}
}
