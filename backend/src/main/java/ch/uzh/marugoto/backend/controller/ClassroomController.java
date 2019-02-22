package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.exception.RequestValidationException;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.dto.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.dto.EditClassroom;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.service.ClassroomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/class")
public class ClassroomController extends BaseController {

    @Autowired
    private ClassroomService classroomService;

    /**
     * List all classes
     * @return
     */
    @ApiOperation(value = "List all classes", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public Iterable<Classroom> listClasses() throws AuthenticationException {
        isSupervisorAuthenticated();
        return classroomService.getClassrooms();
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Show class information", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("{classId}")
    public Object viewClass(@PathVariable String classId) throws AuthenticationException {
        isSupervisorAuthenticated();
        return classroomService.getClassroom("classroom/".concat(classId));
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Create new class", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Classroom createClass(@Validated CreateClassroom newClassroom, BindingResult result) throws AuthenticationException, RequestValidationException, DtoToEntityException {
        isSupervisorAuthenticated();
        if (result.hasErrors()) {
            throw new RequestValidationException(result.getFieldErrors());
        }
        return classroomService.createClassroom(newClassroom, getAuthenticatedUser());
    }

    /**
     * Edit selected class
     * @return class that is updated
     */
    @ApiOperation(value = "Edit class", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "{classId}", method = RequestMethod.PUT)
    public Classroom editClass(@PathVariable String classId, @Validated EditClassroom classroom, BindingResult result) throws AuthenticationException, RequestValidationException, DtoToEntityException {
        isSupervisorAuthenticated();
        if (result.hasErrors()) {
            throw new RequestValidationException(result.getFieldErrors());
        }
        return classroomService.editClassroom("classroom/".concat(classId), classroom);
    }

    /**
     * Invite student to specific class
     * @return student
     */
    @ApiOperation(value = "Invite student to class", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "{classId}/invite/{studentId}", method = RequestMethod.PUT)
    public Object addStudent(@PathVariable String classId, @PathVariable String studentId) throws AuthenticationException {
        isSupervisorAuthenticated();
        // TODO implement functionality and change response object
        return null;
    }

    /**
     * Download all student notebooks within a class
     * @return zip file
     */
    @ApiOperation(value = "Download all student notebooks within a class", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("{classId}/notebooks")
    public Object downloadNotebooks(@PathVariable String classId) throws AuthenticationException {
        isSupervisorAuthenticated();
        // TODO implement functionality and change response object
        return null;
    }
}
