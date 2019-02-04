package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.service.UserMailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class MailController extends BaseController {

	@Autowired
	private UserMailService userMailService;

	@ApiOperation(value = "List all emails where player walked through", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mail/list")
	public List<Mail>getAllMails() throws AuthenticationException {
		return userMailService.getAllMailsWithUserReplies(getAuthenticatedUser());
	}
	
	@ApiOperation (value ="Send mail reply", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/send/notification/{mailId}", method = RequestMethod.PUT)
	public void sendReplyMail(@ApiParam("ID of mail exercise") @PathVariable String mailId, @ApiParam ("Mail reply text") @RequestParam String replyText) throws AuthenticationException {
		userMailService.replyMail(getAuthenticatedUser(), "notification/" + mailId, replyText);
	}
}
