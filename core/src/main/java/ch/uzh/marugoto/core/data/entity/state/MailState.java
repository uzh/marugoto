package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailReply;

@Document
@JsonIgnoreProperties({"id", "user"})
public class MailState {
    @Id
    private String Id;
    private boolean read;
    @Ref
    private Mail mail;
    @Ref
    private User user;
    private List<MailReply> mailReplyList;

    @PersistenceConstructor
    public MailState(Mail mail, User user) {
        super();
        this.mail = mail;
        this.user = user;
        this.mailReplyList = new ArrayList<>();
    }

    public String getId() {
        return Id;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public List<MailReply> getMailReplyList() {
        return mailReplyList;
    }

    public void addMailReply(MailReply mailReply) {
        this.read = true;
        this.mailReplyList.add(mailReply);
    }
}