package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Transient;

import java.util.List;

public class Mail extends Notification {

    private String subject;
    private String body;
    @Transient
    private List<UserMail> replies;

    public Mail() {
        super();
    }

    public Mail(String subject, String body, Page page, Character from) {
        super(page, from);
        this.subject = subject;
        this.body = body;
    }

    public Mail(String subject, String body, Page page, Character from, VirtualTime receiveTimer) {
        super(receiveTimer, page, from);
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<UserMail> getReplies() {
        return replies;
    }

    public void setReplies(List<UserMail> replies) {
        this.replies = replies;
    }
}
