package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class Mail extends Notification {
    private String subject;
    private String body;
    @Ref
    private Character from;

    public Mail() {
        super();
    }

    public Mail(String subject, String body, Character from) {
        this();
        this.subject = subject;
        this.body = body;
        this.from = from   ;
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

    public Character getFrom() {
        return from;
    }

    public void setFrom(Character from) {
        this.from = from;
    }
}
