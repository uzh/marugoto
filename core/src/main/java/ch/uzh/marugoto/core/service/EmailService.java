package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;

@Service
public class EmailService implements IEmailService{
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Messages messages;
	
	@Async
	public void sendEmail(String toAddress, String fromAddress, String resetLink) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(fromAddress);
		mail.setTo(toAddress);
		mail.setSubject(messages.get("mailSubject"));
		mail.setText(messages.get("mailPasswordResetText")+ "\n" + resetLink );
		mailSender.send(mail);
	}
}
