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
    private ReceiveNotificationOption receiveNotificationOption;
    private VirtualTime receiveTimer;
    @Ref
    private Page page;

    public Notification() {
        super();
    }

    public String getId() {
        return id;
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
