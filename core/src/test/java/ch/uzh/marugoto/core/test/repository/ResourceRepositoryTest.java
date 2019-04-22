package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;


public class ResourceRepositoryTest extends BaseCoreTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void testCreateResourceAndFindByPath() {
        var resourcePath = "/dummy-path/image.png";
        var resourceToTest = resourceRepository.save(new ImageResource(resourcePath));

        var resource = resourceRepository.findByPath(resourcePath);
        assertEquals(resourceToTest.getId(), resource.getId());
    }
}
