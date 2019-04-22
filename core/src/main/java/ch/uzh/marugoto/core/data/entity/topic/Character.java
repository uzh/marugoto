package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;

import java.util.Objects;

@Document
public class Character {
    @Id
    private String id;
    private String salutation;
    private String firstName;
    private String lastName;
    private String mail;
    @Ref
    private ImageResource image;

    public Character() {
        super();
    }

    public Character(String salutation, String firstName, String lastName, String mail) {
        this();
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getName() {
        var sal = Objects.toString(salutation, "");
        var fName = Objects.toString(firstName, "");
        var lName = Objects.toString(lastName, "");
        return sal + " " + fName + " " + lName;
    }
}
