package ch.uzh.marugoto.backend.data.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;

@Document("user")
@HashIndex(fields = { "mail" }, unique = true)
public class User {
	@Id
	private String id;

	private Salutation salutation;
	private String firstName;
	private String lastName;
	private String mail;
	private Date signedUpAt;
	private Date lastLoginAt;
	private Date activatedAt;
	private Boolean isSupervisor;

	
	
	
	public User() {
		super();
	}

	public User(final Salutation salutation, final String firstName, final String lastName, final String mail) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}
}