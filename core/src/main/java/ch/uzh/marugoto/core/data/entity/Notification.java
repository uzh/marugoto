package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

@Document("notification")
@JsonIgnoreProperties({"page"})
abstract public class Notification {
    @Id
    private String id;
    @Ref
    private Character from;
    private VirtualTime receiveAfter;
    private boolean openOnReceive;
    @Ref
    private Page page;

    public Notification() {
        super();
    }

    public Notification(Page page, Character character) {
        this();
        this.page = page;
        this.from = character;
    }

    public Notification(VirtualTime receiveAfter, Page page, Character character) {
        this(page, character);
        this.openOnReceive = false;
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

    public boolean isOpenOnReceive() {
        return openOnReceive;
    }

    public void setOpenOnReceive(boolean openOnReceive) {
        this.openOnReceive = openOnReceive;
    }

    public VirtualTime getReceiveAfter() {
        return receiveAfter;
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
