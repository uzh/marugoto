package ch.uzh.marugoto.core.data.resource;

import ch.uzh.marugoto.core.data.entity.application.Classroom;

public class ClassroomList {
    private Iterable<Classroom> classrooms;
    private int numberOfUsers;

    public Iterable<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(Iterable<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}
