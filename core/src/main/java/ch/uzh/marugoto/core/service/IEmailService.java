package ch.uzh.marugoto.core.service;

public interface IEmailService {
	
	public void sendEmail(String toAddress, String fromAddress, String resetLink); 
}
