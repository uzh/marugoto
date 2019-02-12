package ch.uzh.marugoto.core.data.entity.state;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;

@Document
@JsonIgnoreProperties({"id", "startedAt", "finishedAt", "lastSavedAt", "virtualTimeBalance"})
public class TopicState {
    @Id
    private String id;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime lastSavedAt;
    private Money moneyBalance;
    private VirtualTime virtualTimeBalance;
    @Ref
    private Topic topic;

    @PersistenceConstructor
    public TopicState(Topic topic) {
        this.topic = topic;
        this.moneyBalance = new Money();
        this.virtualTimeBalance = new VirtualTime(true);
        this.startedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDateTime getLastSavedAt() {
        return lastSavedAt;
    }

    public void setLastSavedAt(LocalDateTime lastSavedAt) {
        this.lastSavedAt = lastSavedAt;
    }

    public double getMoneyBalance() {
        return moneyBalance.getAmount();
    }

    public void setMoneyBalance(double amount) {
        this.moneyBalance = new Money(amount);
    }

    @JsonGetter
    public long getTimeBalance() {
        return virtualTimeBalance.getTime().toSeconds();
    }

    public Duration getVirtualTimeBalance() {
        return virtualTimeBalance.getTime();
    }

    public void setVirtualTimeBalance(Duration virtualTimeBalance) {
        this.virtualTimeBalance.setTime(virtualTimeBalance);
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TopicState that = (TopicState) o;
        return topic.equals(that.topic);
    }
}
