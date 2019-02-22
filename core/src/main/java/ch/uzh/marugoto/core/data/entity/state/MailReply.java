package ch.uzh.marugoto.core.data.entity.state;

import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;

public class MailReply {

    private String body;
    private LocalDateTime repliedAt;

    @PersistenceConstructor
    public MailReply(String body) {
        super();
        this.body = body;
        this.repliedAt = LocalDateTime.now();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(LocalDateTime repliedAt) {
        this.repliedAt = repliedAt;
    }
}
