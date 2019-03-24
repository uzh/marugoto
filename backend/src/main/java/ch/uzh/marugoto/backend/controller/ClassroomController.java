package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.exception.RequestValidationException;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.dto.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.dto.EditClassroom;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.service.NotebookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("api/classroom")
public class ClassroomController extends BaseController {

    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private NotebookService notebookService;

    /**
     * List all classes
     * @return
     */
    @ApiOperation(value = "List all classes. Needs supervisor privilege.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public Iterable<Classroom> listClasses() throws AuthenticationException {
        return classroomService.getClassrooms(getAuthenticatedUser());
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Show class information. Needs supervisor privilege.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("{classId}")
    public Object viewClass(@PathVariable String classId) {
        return classroomService.getClassroom("classroom/".concat(classId));
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Create new class. Needs supervisor privilege.", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Classroom createClass(@Validated @RequestBody CreateClassroom classroom, BindingResult result) throws AuthenticationException, RequestValidationException, DtoToEntityException {
        if (result.hasErrors()) {
            throw new RequestValidationException(result.getFieldErrors());
        }
        return classroomService.createClassroom(classroom, getAuthenticatedUser());
    }

    /**
     * Edit selected class
     * @return class that is updated
     */
    @ApiOperation(value = "Edit class. Needs supervisor privilege.", authorizations = { @Authorization(value = "apiKey")})
    @RequestMapping(value = "{classId}", method = RequestMethod.PUT)
    public Classroom editClass(@PathVariable String classId, @RequestBody @Validated EditClassroom classroom, BindingResult result) throws RequestValidationException, DtoToEntityException {
        if (result.hasErrors()) {
            throw new RequestValidationException(result.getFieldErrors());
        }
        return classroomService.editClassroom("classroom/".concat(classId), classroom);
    }

    /**
     * List all students
     * @return students
     */
    @ApiOperation(value = "List all classroom members", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("{classId}/members")
    public List<User> listClassroomMembers(@PathVariable String classId) {
        return classroomService.getClassroomMembers("classroom/".concat(classId));
    }

    /**
     * Download compressed file with students notebook within a class
     * @return zip file notebooks.zip
     */
    @ApiOperation(value = "Download compressed file with students notebook within a class. Needs supervisor privilege.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping(value = "{classId}/notebooks", produces = "application/zip")
    public ResponseEntity<InputStreamResource> downloadNotebooks(@PathVariable String classId) throws AuthenticationException, CreateZipException, CreatePdfException {
        var classroomId = "classroom/".concat(classId);
        FileInputStream zip = notebookService.getClassroomNotebooks(classroomService.getClassroomMembers(classroomId), classroomService.getClassroom(classroomId));
        InputStreamResource streamResource = new InputStreamResource(zip);

        log.info(String.format("%s has downloaded notebooks zip file for classroom ID %s", getAuthenticatedUser().getName(), classId));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=notebooks.zip").body(streamResource);
    }
}
