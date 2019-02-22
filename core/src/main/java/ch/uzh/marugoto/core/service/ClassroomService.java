package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.dto.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.dto.EditClassroom;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.helpers.DtoHelper;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    public Iterable<Classroom> getClassrooms() {
        return classroomRepository.findAll();
    }

    public Classroom createClassroom(CreateClassroom classroomRequest, User user) throws DtoToEntityException {
        Classroom classroom = new Classroom();
        DtoHelper.map(classroomRequest, classroom);
        classroom.setCreatedBy(user);
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
}
