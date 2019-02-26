package ch.uzh.marugoto.core.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.User;

public class MailableService {

    @Value("${marugoto.fromMail}")
    private String fromMail;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Messages messages;


    public void sendResetPasswordEmail(String userMail, String resetLink) throws MessagingException {
        var subject = messages.get("mailPasswordResetSubject");
        String link = "<a href="+resetLink+" target=\"_blank\">link</a>";
        var message = messages.get("mailPasswordResetText")+ "\n" + link;
        sendMail(userMail, subject, message);
    }

    @Async
    public void sendMail(String toAddress, String subject, String message) throws MessagingException {
        
    	//SimpleMailMessage m = new SimpleMailMessage();
    	MimeMessage mimeMsg = mailSender.createMimeMessage();
    	MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, false, "utf-8");

    	helper.setFrom(fromMail);
    	helper.setTo(toAddress);
    	helper.setSubject(subject);
    	helper.setText(message,true);

        mailSender.send(mimeMsg);
    }
}
