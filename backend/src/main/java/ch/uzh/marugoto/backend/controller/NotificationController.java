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
import ch.uzh.marugoto.core.data.entity.RepliedMail;
import ch.uzh.marugoto.core.exception.RepliedMailNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class NotificationController extends BaseController {

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private NotebookService notebookService;
	private final String notificationPrefixId = "notification/";
	
	@ApiOperation(value = "List all emails where player walked through", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mail/list")
	public List<RepliedMail>getAllUserMails() throws AuthenticationException {
		return notificationService.getAllRepliedMails(getAuthenticatedUser());
	}
	
	@ApiOperation(value = "Load email by its id", authorizations = { @Authorization(value = "apiKey")})
	@GetMapping("mail/findReply/notification/{mailId}")
	public RepliedMail getUserMail(@ApiParam("Mail ID") @PathVariable String mailId) throws AuthenticationException, RepliedMailNotFoundException {
		return notificationService.getRepliedMail(getAuthenticatedUser(), notificationPrefixId + mailId);
	}
	
	@ApiOperation (value ="Send mail reply", authorizations = { @Authorization(value = "apiKey")})
	@RequestMapping(value = "mail/send/notification/{mailId}", method = RequestMethod.PUT)
	public void sendReplyMail(@ApiParam("ID of mail exercise") @PathVariable String mailId, @ApiParam ("Mail reply text") @RequestParam String replyText) throws AuthenticationException {
		Mail mail = notificationService.findMailById(notificationPrefixId + mailId);
		notificationService.sendReplyMail(getAuthenticatedUser(), mail, replyText);
		notebookService.addNotebookEntryForMail(getAuthenticatedUser().getCurrentPageState(), mail);
	}
}
