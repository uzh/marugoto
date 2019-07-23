package ch.uzh.marugoto.backend.resource;

public class ShibbolethUser {

	/**
	 * These fields are International SAML attributes
	 */
	private String commonName;
	private String displayName;
	/**
	 * These fields are SWITCHaai specific attributes
	 */
	private String email;
	private String eduPersonAffiliation;
	private String givenName;
	private String surname;

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEduPersonAffiliation() {
		return eduPersonAffiliation;
	}

	public void setEduPersonAffiliation(String eduPersonAffiliation) {
		this.eduPersonAffiliation = eduPersonAffiliation;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
