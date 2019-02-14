package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

import java.time.Duration;

@Document("notification")
@JsonIgnoreProperties({"page"})
abstract public class Notification {
    @Id
    private String id;
    @Ref
    private Character from;
    private VirtualTime receiveAfter;

    @Ref
    private Page page;

    public Notification() {
        super();
        this.receiveAfter = new VirtualTime(Duration.ZERO, true);
    }

    public Notification(Page page, Character character) {
        this();
        this.page = page;
        this.from = character;
    }

    public Notification(VirtualTime receiveAfter, Page page, Character character) {
        this(page, character);
        this.receiveAfter = receiveAfter;
    }

    public String getId() {
        return id;
    }

    public Character getFrom() {
        return from;
    }

    public void setFrom(Character from) {
        this.from = from;
    }

    public long getReceiveAfter() {
        return receiveAfter.getTime().toSeconds();
    }

    public void setReceiveAfter(VirtualTime receiveAfter) {
        this.receiveAfter = receiveAfter;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
