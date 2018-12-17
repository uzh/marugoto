package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
