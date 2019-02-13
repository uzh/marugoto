package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;

import ch.uzh.marugoto.core.data.entity.state.UserMail;

import org.springframework.data.annotation.Transient;

public class Mail extends Notification {

    private String subject;
    private String body;
    @Ref
    private PageTransition pageTransition;
    @Transient
    private UserMail replied;
    @Transient
    private boolean read;

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

    public boolean hasTransition() {
        return pageTransition != null;
    }

    public PageTransition getPageTransition() {
        return pageTransition;
    }

    public void setPageTransition(PageTransition pageTransition) {
        this.pageTransition = pageTransition;
    }

    @JsonGetter
    public UserMail getReplied() {
        return replied;
    }

    public void setReplied(UserMail replied) {
        this.replied = replied;
    }

    @JsonGetter
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
