package ch.uzh.marugoto.core.service;

public interface EmailService {
	void sendEmail(String toAddress, String subject, String message);
}
