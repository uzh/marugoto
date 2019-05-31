package ch.uzh.marugoto.backend.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ch.uzh.marugoto.core.service.UploadExerciseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
 
@RestController
public class UploadController extends BaseController {

    @Autowired
    private UploadExerciseService uploadExerciseService;

    @ApiOperation(value = "Finds file by exercise ID", authorizations = {@Authorization("apiKey")})
    @GetMapping(value = "uploads/exerciseState/{id}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getFileByExerciseId(@ApiParam("ID of ExerciseState") @PathVariable String id) throws IOException {

    	File file = uploadExerciseService.getFileByExerciseId("exerciseState/" + id);
    	InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    	
    	return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .body(resource); 
    }
    
    @ApiOperation(value = "Uploads new file to the server", authorizations = {@Authorization("apiKey")})
	@RequestMapping(value = "uploads", method = RequestMethod.POST)
    public void uploadFile(@ApiParam("File for upload") @RequestParam("file") MultipartFile file, 
    					   @ApiParam("exerciseStateId") @RequestParam("exerciseStateId") String exerciseStateId) throws Exception {
    	if (file.isEmpty()) {
    		throw new FileUploadException();
    	}
    	uploadExerciseService.uploadFile(file, exerciseStateId);
    }
    
    @ApiOperation(value = "Delete the file from server", authorizations = {@Authorization("apiKey")})
    @RequestMapping(value = "uploads/exerciseState/{id}",method = RequestMethod.DELETE)
    public void deleteFile(@PathVariable("id") String id) throws Exception {
    	uploadExerciseService.deleteFile("exerciseState/" + id);
    }
    
}
