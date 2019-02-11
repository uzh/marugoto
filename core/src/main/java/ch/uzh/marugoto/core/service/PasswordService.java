package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;

@Service
public class PasswordService extends MailableService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private Messages messages;

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public void sendResetPasswordEmail(String toAddress, String resetLink) {
		var subject = messages.get("mailPasswordResetSubject");
		var message = messages.get("mailPasswordResetText")+ "\n" + resetLink;
		sendMail(toAddress, subject, message);
	}
}
