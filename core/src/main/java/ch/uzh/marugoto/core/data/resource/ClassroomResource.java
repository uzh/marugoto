package ch.uzh.marugoto.core.data.resource;

import ch.uzh.marugoto.core.data.entity.application.Classroom;

public class ClassroomResource {
    private Classroom classroom;
    private int numberOfUsers;

    public Classroom getClassroms() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}
