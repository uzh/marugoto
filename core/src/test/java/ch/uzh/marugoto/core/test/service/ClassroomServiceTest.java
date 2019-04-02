package ch.uzh.marugoto.core.test.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.dto.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.dto.EditClassroom;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ClassroomServiceTest extends BaseCoreTest {

    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private ClassroomRepository classroomRepository;
    private CreateClassroom newClassroom;

    public synchronized void before() {
        super.before();
        newClassroom = new CreateClassroom();
        newClassroom.setName("Test Class");
        newClassroom.setStartClassAt(LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
        newClassroom.setEndClassAt(LocalDate.now().plusDays(10).format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
    }

    @Test
    public void testCreateClassroom() throws Exception {
        assertFalse(classroomRepository.findAll().iterator().hasNext());
        classroomService.createClassroom(newClassroom, user);
        assertTrue(classroomRepository.findAll().iterator().hasNext());
        assertEquals(classroomRepository.findAll().iterator().next().getName(), "Test Class");
    }

    @Test
    public void testEditClassroom() throws Exception {
        var createdClass = classroomService.createClassroom(newClassroom, user);

        EditClassroom editClassroom = new EditClassroom();
        editClassroom.setName("Edit Name");
        var editedClass = classroomService.editClassroom(createdClass.getId(), editClassroom);

        assertEquals(createdClass.getId(), editedClass.getId());
        assertNotEquals(createdClass.getName(), editedClass.getName());
    }

    @Test
    public void testGetClassroom() throws Exception {
        var createdClass = classroomService.createClassroom(newClassroom, user);
        var testClass = classroomService.getClassroom(createdClass.getId());
        assertNotNull(classroomService.getClassroom(createdClass.getId()));
        assertEquals(testClass.getId(), createdClass.getId());
    }

    @Test
    public void testAddUserToClassroomAndGetClassroomMembers() throws Exception {
        var createdClass = classroomService.createClassroom(newClassroom, user);
        assertEquals(0, classroomService.getClassroomMembers(createdClass.getId()).size());
        classroomService.addUserToClassroom(user, createdClass.getInvitationLinkId());
        assertEquals(1, classroomService.getClassroomMembers(createdClass.getId()).size());
    }

    @Test
    public void testCreateClassroomInvitationLink() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ClassroomService.class.getDeclaredMethod("createInvitationLinkForClassroom");
        method.setAccessible(true);
        String link = (String) method.invoke(classroomService);
        int testLength = Constants.INVITATION_LINK_LENGTH + Constants.INVITATION_LINK_PREFIX.length();
        assertEquals(testLength, link.length());
        assertTrue(link.contains(Constants.INVITATION_LINK_PREFIX));
    }
}
