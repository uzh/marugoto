package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

@Document()
@JsonIgnoreProperties({ "id", "user", "mail"})
public class UserMail {
    @Id
    private String id;
    private String text;
    @Ref
    private Mail mail;
    @Ref
    private User user;

    public UserMail() {
        super();
    }

    public UserMail(Mail mail, User user) {
        this();
        this.mail = mail;
        this.user = user;
    }

    public UserMail(Mail mail, User user, String text) {
        this(mail, user);
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
