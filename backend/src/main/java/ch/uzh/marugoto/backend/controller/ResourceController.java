package ch.uzh.marugoto.backend.controller;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.service.ResourceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class ResourceController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    @ApiOperation(value = "Finds resource file by ID", authorizations = {@Authorization("apiKey")})
    @GetMapping("resources/resource/{resourceId}")
    public Resource findResource(@ApiParam("ID of resource file") @PathVariable String resourceId) throws ResourceNotFoundException {
        Resource resource = resourceService.getById(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException(resourceId);
        }
        return resource;
    }
    
    @ApiOperation(value = "Uploads new file to the server", authorizations = {@Authorization("apiKey")})
	@RequestMapping(value = "resources/resource/upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadResource(@ApiParam("File for upload") @RequestParam("file") MultipartFile file) throws FileUploadException, IOException {
    	
    	if (file.isEmpty()) {
    		throw new FileUploadException();
    	}
    	resourceService.storeFile(file);
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @ApiOperation(value = "Delete file from server", authorizations = {@Authorization("apiKey")})
    @RequestMapping(value = "resources/resource/{resourceId}",method=RequestMethod.DELETE)
    public ResponseEntity<Resource> deleteResource(@ApiParam("File for delete") @RequestParam("resourceId") String resourceId) throws IOException {
    	resourceService.deleteFile("resource/" + resourceId);
    	return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
    }
    
}
