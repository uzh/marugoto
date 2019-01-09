package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class MailExercise extends Exercise {

	private String subject;
	private String mailBody;
	@Ref
	private Character isFrom;
	
	public MailExercise() {
		super();
	}
	
	public MailExercise (int numberOfColumns, Page page, String subject, String mailBody, Character isFrom) {
		super(numberOfColumns,page);
		this.subject = subject;
		this.mailBody = mailBody;
		this.isFrom = isFrom;
	}

	public String getSubject() {
		return subject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public Character getFrom() {
		return isFrom;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public void setFrom(Character isFrom) {
		this.isFrom = isFrom;
	}
}
