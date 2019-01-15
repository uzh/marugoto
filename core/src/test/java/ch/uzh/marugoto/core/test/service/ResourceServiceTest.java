package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ResourceServiceTest extends BaseCoreTest {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceService resourceService;
    
    @Test
    public void testGetById() {
        var resource = resourceRepository.findAll().iterator().next();
        var testResource = resourceService.getById(resource.getId());

        assertEquals(resource.getId(), testResource.getId());
    }
    
}
