package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;

@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Messages messages;

	@Value("${marugoto.fromMail}")
	private String fromMail;

	public void sendResetPasswordEmail(String toAddress, String resetLink) {
		var subject = messages.get("mailPasswordResetSubject");
		var message = messages.get("mailPasswordResetText")+ "\n" + resetLink;
		sendEmail(toAddress, subject, message);
	}

	@Override
	@Async
	public void sendEmail(String toAddress, String subject, String message) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(fromMail);
		mail.setTo(toAddress);
		mail.setSubject(subject);
		mail.setText(message);
		mailSender.send(mail);
	}
}
