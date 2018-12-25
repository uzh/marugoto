package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ResourceServiceTest extends BaseCoreTest {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private FileService fileService;

    @Test
    public void testGetById() {
        var resource = resourceRepository.findAll().iterator().next();
        var testResource = resourceService.getById(resource.getId());

        assertEquals(resource.getId(), testResource.getId());
    }

    @Test
    public void testStoreRenameAndDeleteFile() throws Exception {

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("demo-storyline/image.jpg");
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/png", inputStream);
        String filePath = resourceService.storeFile(file);
        var expectedPath = fileService.getUploadDir()+ File.separator + file.getOriginalFilename();
        assertNotNull(filePath);
        assertEquals(expectedPath , filePath);
        
        //rename
        Resource resource = resourceService.renameResource(filePath, "testExercisice/123");
        expectedPath = fileService.getUploadDir()+ File.separator + "123.jpg";
        assertEquals(expectedPath, resource.getPath());
        
        //delete 
        resourceService.deleteFile(resource.getId());
        assertNull(resourceRepository.findByPath(resource.getPath()));
    }
    
    @Test
    public void testGetResourceType() {
    	var imageResourceName = "resource.jpg";
    	assertThat(resourceService.getResourceType(imageResourceName), instanceOf(ImageResource.class));
    	
    	var docResourceName = "resource.doc";
    	assertThat(resourceService.getResourceType(docResourceName), instanceOf(DocumentResource.class));
    	
    	var pdfResourceName = "resource.pdf";
    	assertThat(resourceService.getResourceType(pdfResourceName), instanceOf(PdfResource.class));
    	
    	var videoResourceName = "resource.mp4";
    	assertThat(resourceService.getResourceType(videoResourceName), instanceOf(VideoResource.class));
    			
    }	
    
}
