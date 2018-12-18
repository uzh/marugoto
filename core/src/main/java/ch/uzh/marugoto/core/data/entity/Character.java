package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

import org.springframework.data.annotation.Id;

@Document
public class Character {
    @Id
    private String id;
    private Salutation salutation;
    private String firstname;
    private String lastname;
    private String mail;
    private ImageResource image;

    public Character() {
        super();
    }

    public Character(Salutation salutation, String firstname, String lastname, String mail) {
        this();
        this.salutation = salutation;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public Salutation getSalutation() {
        return salutation;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public ImageResource getImage() {
        return image;
    }

    public void setImage(ImageResource image) {
        this.image = image;
    }
}
