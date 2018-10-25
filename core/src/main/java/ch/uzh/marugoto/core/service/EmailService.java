package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService{
	@Autowired
	private JavaMailSender mailSender;
	
	@Async
	public void sendEmail(String toAddress, String fromAddress, String resetLink) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(fromAddress);
		mail.setTo(toAddress);
		mail.setSubject("Password Reset Request");
		mail.setText("To reset your password, click the link below:\n" + resetLink );
		mailSender.send(mail);
	}
}
