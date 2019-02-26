package ch.uzh.marugoto.core.service;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.User;

@Service
public class PasswordService extends MailableService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private Messages messages;

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public void sendResetPasswordEmail(User user, String passwordResetUrl) throws MessagingException {
		String resetLink = passwordResetUrl + "?mail=" + user.getMail() +"&token=" + user.getResetToken();
		
		var subject = messages.get("mailPasswordResetSubject");
		String link = "<a href="+resetLink+" target=\"_blank\">link</a>";
		var message = messages.get("mailPasswordResetText")+ "\n" + link;		
		sendMail(user.getMail(), subject, message);
	}
}
