package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    protected static Resource getFromPath(String resourcePath) throws ResourceNotFoundException {
        if (new File(resourcePath).exists()) {
            return new ImageResource(resourcePath);
        } else {
            throw new ResourceNotFoundException(resourcePath);
        }
    }

    public Resource getById(String id) {
        return resourceRepository.findById(id).orElse(null);
    }
}
