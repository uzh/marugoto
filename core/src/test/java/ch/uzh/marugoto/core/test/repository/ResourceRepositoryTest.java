package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.topic.PdfResource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;


public class ResourceRepositoryTest extends BaseCoreTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void testCreateResourceAndFindByPath() {
        var resourcePath = "/dummy-path/pdf.pdf";
        var resourceToTest = resourceRepository.save(new PdfResource(resourcePath));

        var resource = resourceRepository.findByPath(resourcePath);
        assertEquals(resourceToTest.getId(), resource.getId());
    }
}
