package ch.uzh.marugoto.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.ClassroomMember;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.resource.EditClassroom;
import ch.uzh.marugoto.core.data.repository.ClassroomMemberRepository;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.data.resource.ClassroomResource;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.helpers.DtoHelper;
import ch.uzh.marugoto.core.helpers.StringHelper;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private ClassroomMemberRepository classroomMemberRepository;

    public List<ClassroomResource> getClassrooms(User user) {
        List<Classroom> classrooms = classroomRepository.findAllByCreatedById(user.getId());
        
        List<ClassroomResource> classroomResources = new ArrayList<ClassroomResource>();
    	for(Classroom classroom : classrooms) {
    		ClassroomResource classroomResource = new ClassroomResource();
    		classroomResource.setClassroom(classroom);
    		List<User> classroomMembers = getClassroomMembers(classroom.getId());
    		classroomResource.setNumberOfUsers(classroomMembers.size());
    	}

    	return classroomResources;
    }

    public Classroom createClassroom(CreateClassroom classroomRequest, User user) throws DtoToEntityException {
        Classroom classroom = new Classroom();
        DtoHelper.map(classroomRequest, classroom);
        classroom.setCreatedBy(user);
        classroom.setInvitationLinkId(createInvitationLinkForClassroom());
        return classroomRepository.save(classroom);
    }

    public Classroom editClassroom(String classId, EditClassroom classroomRequest) throws DtoToEntityException {
        Classroom classroom = classroomRepository.findById(classId).orElseThrow();
        DtoHelper.map(classroomRequest, classroom);
        return classroomRepository.save(classroom);
    }

    public Classroom getClassroom(String classId) {
        return classroomRepository.findById(classId).orElseThrow();
    }

    public Classroom addUserToClassroom(User user, String invitationLink) {
        Classroom classroom = classroomRepository.findByInvitationLink(invitationLink).orElseThrow();
        User member = classroomMemberRepository.findMemberOfClassroom(user.getId(), classroom.getId());

        if (member == null) {
            classroomMemberRepository.save(new ClassroomMember(classroom, user));
        }
        return classroom;
    }

    public List<User> getClassroomMembers(String classroomId) {
        return classroomMemberRepository.findClassroomMembers(classroomId);
    }

    public String createInvitationLinkForClassroom() {
        String prefix = Constants.INVITATION_LINK_PREFIX;
        String randomString = StringHelper.generateRandomString(Constants.INVITATION_LINK_LENGTH);
        return prefix.concat(randomString);
    }
    
    public boolean classHasExpired(Classroom classroom) {
    	boolean isExpired = false;
    	var currentDate = LocalDate.now();
    	if (currentDate.isAfter(classroom.getEndClassAt()) || currentDate.isBefore(classroom.getStartClassAt())) {
    		isExpired = true;
    	}
    	return isExpired;
    }
}
