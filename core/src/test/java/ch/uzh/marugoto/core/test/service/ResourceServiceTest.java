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

    //@Test
    //public void testUploadRenameAndDeleteFile() throws Exception {
  //      InputStream inputStream = new URL("https://picsum.photos/600/?random").openStream();
 //       MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpg", inputStream);

//        String filePath = resourceService.uploadResource(file);
//        var expectedPath = fileService.getUploadDir() + File.separator + file.getOriginalFilename();
//        assertNotNull(filePath);
//        assertEquals(expectedPath , filePath);
//        
//        //rename
//        Resource resource = resourceService.renameResource(filePath, "testExercise/123");
//        expectedPath = fileService.getUploadDir() + File.separator + "123.jpg";
//        assertEquals(expectedPath, resource.getPath());
//        
//        //delete 
//        resourceService.deleteResource(resource.getId());
//        assertNull(resourceRepository.findByPath(resource.getPath()));
 //   }
    
}
