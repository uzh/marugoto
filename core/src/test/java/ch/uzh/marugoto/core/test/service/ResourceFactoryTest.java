package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ResourceFactoryTest extends BaseCoreTest{

   @Test
    public void testGetResource() throws ResourceTypeResolveException {
    	var imageResourceName = "resource.jpg";
    	assertThat(ResourceFactory.getResource(imageResourceName), instanceOf(ImageResource.class));
    	
    	var docResourceName = "resource.doc";
    	assertThat(ResourceFactory.getResource(docResourceName), instanceOf(DocumentResource.class));
    	
    	var pdfResourceName = "resource.pdf";
    	assertThat(ResourceFactory.getResource(pdfResourceName), instanceOf(PdfResource.class));
    	
    	var videoResourceName = "resource.mp4";
    	assertThat(ResourceFactory.getResource(videoResourceName), instanceOf(VideoResource.class));
    			
    }	
}
