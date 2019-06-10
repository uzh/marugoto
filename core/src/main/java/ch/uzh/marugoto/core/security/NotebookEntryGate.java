package ch.uzh.marugoto.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.repository.ClassroomMemberRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;

@Component
public class NotebookEntryGate implements ModelGate {

    @Autowired
    private ClassroomMemberRepository classroomMemberRepository;
    @Autowired
    private NotebookEntryStateRepository notebookEntryStateRepository;

    @Override
    public boolean canCreate(User user) {
        return false;
    }

    @Override
    public boolean canRead(User user, Object objectModel) {
        Classroom classroom = (Classroom) objectModel;
        List<User> classroomMembers = classroomMemberRepository.findClassroomMembers(classroom.getId());
         return classroom.getCreatedBy().equals(user) || classroomMembers.stream().anyMatch(classroomMember -> classroomMember.equals(user));
    }

    @Override
    public boolean canUpdate(User user, Object objectModel) {
        var notebookContentId = objectModel.toString();
        NotebookEntryState notebookEntryState = notebookEntryStateRepository.findNotebookEntryStateByNotebookContent(notebookContentId);
        return notebookEntryState.getGameState().getUser().equals(user);
    }

    @Override
    public boolean canDelete(User user, Object objectModel) {
        return false;
    }
}
