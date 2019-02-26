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
}
