package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public class MailableService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${marugoto.fromMail}")
    private String fromMail;

    @Async
    public void sendMail(String toAddress, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromMail);
        mail.setTo(toAddress);
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }
}
