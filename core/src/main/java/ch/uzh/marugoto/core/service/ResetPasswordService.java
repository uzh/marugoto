package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;

@Service
public class ResetPasswordService extends MailableService {

	@Autowired
	private Messages messages;

	public void sendResetPasswordEmail(String toAddress, String resetLink) {
		var subject = messages.get("mailPasswordResetSubject");
		var message = messages.get("mailPasswordResetText")+ "\n" + resetLink;
		sendMail(toAddress, subject, message);
	}
}
