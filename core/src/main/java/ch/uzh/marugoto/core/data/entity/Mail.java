package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Transient;

import java.util.List;

public class Mail extends Notification {

    private String subject;
    private String body;
    @Ref
    private PageTransition pageTransition;
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

    public boolean hasTransition() {
        return pageTransition != null;
    }

    public PageTransition getPageTransition() {
        return pageTransition;
    }

    public void setPageTransition(PageTransition pageTransition) {
        this.pageTransition = pageTransition;
    }

    public List<UserMail> getReplies() {
        return replies;
    }

    public void setReplies(List<UserMail> replies) {
        this.replies = replies;
    }
}
