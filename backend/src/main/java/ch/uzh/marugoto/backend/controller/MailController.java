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

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.UserMail;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.service.MailService;
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
	public List<Mail>getAllMails() throws AuthenticationException {
		return mailService.getReceivedMails(getAuthenticatedUser());
	}

	@ApiOperation (value ="New mail", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/sync/notification/{mailId}", method = RequestMethod.PUT)
	public void newMail(@ApiParam("ID of mail") @PathVariable String mailId) throws AuthenticationException {
		mailService.receiveMail("notification/" + mailId, getAuthenticatedUser());
	}
	
	@ApiOperation (value ="Send mail reply", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/reply/notification/{mailId}", method = RequestMethod.PUT)
	public HashMap<String, Object> replyMail(@ApiParam("ID of mail exercise") @PathVariable String mailId, @ApiParam ("Mail reply text") @RequestParam String replyText) throws AuthenticationException, PageTransitionNotAllowedException {
		var user = getAuthenticatedUser();
		var response = new HashMap<String, Object>();
		response.put("stateChanged", false);

		UserMail userMail = mailService.replyOnMail(user, "notification/" + mailId, replyText);

		if (userMail.getMail().hasTransition()) {
			stateService.doPageTransition(TransitionChosenOptions.player, userMail.getMail().getPageTransition().getId(), user);
			response.replace("stateChanged", true);
		}

		return response;
	}
}
