package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.topic.Mail;

@Document
@JsonIgnoreProperties({"id"})
public class MailState {
    @Id
    private String Id;
    private boolean read;
    @Ref
    @JsonIgnoreProperties({"openOnReceive", "pageTransition", "receiveAfter", "page"})
    private Mail mail;
    @Ref
    private GameState gameState;
    private LocalDateTime createdAt;
    @Ref
    private List<MailReply> mailReplyList;

    @PersistenceConstructor
    public MailState(Mail mail, GameState gameState) {
        super();
        this.mail = mail;
        this.gameState = gameState;
        this.mailReplyList = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
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

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<MailReply> getMailReplyList() {
        return mailReplyList;
    }

    public void addMailReply(MailReply mailReply) {
        this.read = true;
        this.mailReplyList.add(0, mailReply);
    }
}
