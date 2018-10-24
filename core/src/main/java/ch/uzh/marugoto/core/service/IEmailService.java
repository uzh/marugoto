package ch.uzh.marugoto.core.service;

public interface IEmailService {
	
	void sendEmail(String toAddress, String fromAddress, String resetLink);
}
