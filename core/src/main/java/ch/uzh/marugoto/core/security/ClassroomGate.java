package ch.uzh.marugoto.core.security;

import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;

@Component
public class ClassroomGate implements ModelGate {

    @Override
    public boolean canCreate(User user) {
        return false;
    }

    @Override
    public boolean canRead(User user, Object objectModel) {
        return isClassCreatedByUser(user, objectModel);
    }

    @Override
    public boolean canUpdate(User user, Object objectModel) {
        return isClassCreatedByUser(user, objectModel);
    }

    @Override
    public boolean canDelete(User user, Object objectModel) {
        return false;
    }


    private boolean isClassCreatedByUser(User user, Object objectModel) {
        Classroom classroom = (Classroom) objectModel;
        return classroom.getCreatedBy().equals(user);
    }
}
