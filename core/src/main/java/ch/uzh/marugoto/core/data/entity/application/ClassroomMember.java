package ch.uzh.marugoto.core.data.entity.application;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;

@Edge
public class ClassroomMember {
    @Id
    private String Id;
    @Ref
    private Classroom from;
    @Ref
    private User to;

    public ClassroomMember() {
        super();
    }

    public ClassroomMember(Classroom from, User to) {
        this();
        this.from = from;
        this.to = to;
    }

    public String getId() {
        return Id;
    }

    public Classroom getFrom() {
        return from;
    }

    public void setFrom(Classroom from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }
}
