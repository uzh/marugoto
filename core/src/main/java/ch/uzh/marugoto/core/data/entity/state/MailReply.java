package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;

@Document
@JsonIgnoreProperties({"id"})
public class MailReply {
    @Id
    private String Id;
    private String body;
    private LocalDateTime repliedAt;

    @PersistenceConstructor
    public MailReply(String body) {
        super();
        this.body = body;
        this.repliedAt = LocalDateTime.now();
    }

    public String getId() {
        return Id;
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
