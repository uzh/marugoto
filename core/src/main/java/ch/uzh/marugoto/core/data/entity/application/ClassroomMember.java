package ch.uzh.marugoto.core.data.entity.application;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;

import org.springframework.data.annotation.Id;

@Edge
public class ClassroomMember {
    @Id
    private String Id;
    @From
    private Classroom classroom;
    @To
    private User member;

    public ClassroomMember() {
        super();
    }

    public ClassroomMember(Classroom classroom, User user) {
        this();
        this.classroom = classroom;
        this.member = user;
    }

    public String getId() {
        return Id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public User getUser() {
        return member;
    }

    public void setUser(User user) {
        this.member = user;
    }
}
