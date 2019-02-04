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
    private ReceiveNotificationOption receiveNotificationOption;
    private VirtualTime receiveTimer;
    @Ref
    private Page page;

    public Notification() {
        super();
    }

    public Notification(Page page, Character character) {
        this();
        this.receiveNotificationOption = ReceiveNotificationOption.pageEnter;
        this.page = page;
        this.from = character;
    }

    public Notification(VirtualTime receiveTimer, Page page, Character character) {
        this(page, character);
        this.receiveNotificationOption = ReceiveNotificationOption.timer;
        this.receiveTimer = receiveTimer;
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

    public ReceiveNotificationOption getReceiveNotificationOption() {
        return receiveNotificationOption;
    }

    public void setReceiveNotificationOption(ReceiveNotificationOption receiveNotificationOption) {
        this.receiveNotificationOption = receiveNotificationOption;
    }

    public VirtualTime getReceiveTimer() {
        return receiveTimer;
    }

    public void setReceiveTimer(VirtualTime receiveTimer) {
        this.receiveTimer = receiveTimer;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
