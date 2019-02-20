package ch.uzh.marugoto.core.data.entity.state;

import org.springframework.data.annotation.PersistenceConstructor;

public class MailReply {

    private String body;

    @PersistenceConstructor
    public MailReply(String body) {
        super();
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
