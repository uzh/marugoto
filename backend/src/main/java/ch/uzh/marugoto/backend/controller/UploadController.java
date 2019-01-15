package ch.uzh.marugoto.backend.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.service.UploadExerciseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
 
@RestController
public class UploadController extends BaseController {

    @Autowired
    private UploadExerciseService uploadExerciseService;

    @ApiOperation(value = "Finds file by exercise ID", authorizations = {@Authorization("apiKey")})
    @GetMapping("uploads/{id}")
    public File getFilebyExerciseId(@ApiParam("ID of ExerciseState") @PathVariable String id) throws FileNotFoundException {
    	return uploadExerciseService.getFileByExerciseId(id);
    }
    
    @ApiOperation(value = "Uploads new file to the server", authorizations = {@Authorization("apiKey")})
	@RequestMapping(value = "uploads", method = RequestMethod.POST)
    public void uploadFile(@ApiParam("File for upload") @RequestParam("file") MultipartFile file, 
    					   @ApiParam("exerciseStateId") @RequestParam("exerciseStateId") String exerciseStateId) throws ParseException, Exception {
    	if (file.isEmpty()) {
    		throw new FileUploadException();
    	}
    	uploadExerciseService.uploadFile(file, exerciseStateId);
    }
    
    @ApiOperation(value = "Delete the file from server", authorizations = {@Authorization("apiKey")})
    @RequestMapping(value = "uploads/{id}",method = RequestMethod.DELETE)
    public void deleteFile(@PathVariable("id") String id) throws ParseException, Exception {
    	uploadExerciseService.deleteFile(id);
    }
    
}
