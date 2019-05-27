package ch.uzh.marugoto.backend.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.exception.RequestValidationException;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.CreateClassroom;
import ch.uzh.marugoto.core.data.entity.resource.EditClassroom;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.exception.DownloadNotebookException;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.service.GameStateService;
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
    @Autowired
    private GameStateService gameStateService;

    /**
     * List all classes
     * @return
     */
    @ApiOperation(value = "List all classes.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("list")
    public Iterable<Classroom> listClasses() throws AuthenticationException {
        return classroomService.getClassrooms(getAuthenticatedUser());
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Show class information.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("{classId}")
    public Object viewClass(@PathVariable String classId) {
    	//return classroomService.getClassroom("classroom/".concat(classId));
    	Map<String, Object> result = new HashMap<String,Object>();
    	Classroom classroom = classroomService.getClassroom("classroom/".concat(classId));
    	GameState gameState = gameStateService.getClassroomGameState(classroom.getId());
    	result.put("gameState", gameState);
    	result.put("classroom", classroom);
    	return new ResponseEntity<Map<String, Object>>(result,HttpStatus.OK);
    }

    /**
     * Create new class
     * @return class that is created
     */
    @ApiOperation(value = "Create new class.", authorizations = { @Authorization(value = "apiKey")})
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
    @ApiOperation(value = "Edit class.", authorizations = { @Authorization(value = "apiKey")})
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
     * Download compressed file with the notebook and uploaded files for a specific user
     * @return zip file student_files.zip
     * @throws DownloadNotebookException 
     * @throws FileNotFoundException 
     */
    @ApiOperation(value = "Download compressed file with the notebook and uploaded files for a specific student.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping(value = "{classId}/files/{studentId}", produces = "application/zip")

    public ResponseEntity<InputStreamResource> downloadNotebookAndFilesForUser(@PathVariable String classId, @PathVariable String studentId) throws AuthenticationException, CreateZipException, CreatePdfException, DownloadNotebookException, FileNotFoundException {
    	
        FileInputStream zip =  notebookService.getCompressedFileForUserByTopic(classId, "user/" + studentId);
        InputStreamResource streamResource = new InputStreamResource(zip);
        log.info(String.format("%s has downloaded notebooks zip file for classroom ID %s", getAuthenticatedUser().getName(), classId));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=student_files.zip").body(streamResource);
    }

    /**
     * Download compressed file with students notebook within a class
     * @return zip file classroom_files.zip
     * @throws DownloadNotebookException 
     * @throws FileNotFoundException 
     */
    @ApiOperation(value = "Download compressed file with notebooks and uploaded files for all students in the class.", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping(value = "{classId}/files", produces = "application/zip")

    public ResponseEntity<InputStreamResource> downloadNotebookAndFilesForClassrom(@PathVariable String classId) throws AuthenticationException, CreateZipException, CreatePdfException, DownloadNotebookException, FileNotFoundException {
    	var students = classroomService.getClassroomMembers("classroom/".concat(classId));
        FileInputStream zip = notebookService.getCompressedFileForClassroom(students, classId);
        InputStreamResource streamResource = new InputStreamResource(zip);

        log.info(String.format("%s has downloaded notebooks zip file for classroom ID %s", getAuthenticatedUser().getName(), classId));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=classroom_files.zip").body(streamResource);
    }
}
