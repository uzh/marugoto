package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ImageServiceTest extends BaseCoreTest {

    private final int imageWidth = 600;
    private final String imageUrl = String.format("https://picsum.photos/%s/?random", imageWidth);

    @Test
    public void testResizeImageFromWidth() throws IOException {
        var resizedWidth = 400;
        var storeImageFolder = FileService.generateFolder(System.getProperty("user.home") + File.separator + "unit-test-folder");
        var imagePath = storeImageFolder + File.separator + "img.jpg";
        ImageIO.write(ImageIO.read(new URL(imageUrl)), "jpg", new File(imagePath));

        var image = ImageService.resizeImage(new ImageResource(imagePath), resizedWidth);
        var resizedImageWidth = ImageService.getImageWidth(image.getPath());

        assertNotEquals(resizedImageWidth, imageWidth);
        assertEquals(resizedWidth, resizedImageWidth);

        FileService.deleteFolder(storeImageFolder.getAbsolutePath());
    }

    @Test
    public void testResizeImageFromColumn() throws IOException, ResourceNotFoundException {
        var storeImageFolder = FileService.generateFolder(System.getProperty("user.home") + File.separator + "unit-test-folder");
        var imagePath = storeImageFolder + File.separator + "img.jpg";
        ImageIO.write(ImageIO.read(new URL(imageUrl)), "jpg", new File(imagePath));

        var columns = 2;
        var image = ImageService.resizeImage(imagePath, columns);
        var resizedImageWidth = ImageService.getImageWidth(image.getPath());

        assertNotEquals(resizedImageWidth, imageWidth);
        assertEquals(ImageService.getImageWidthForColumns(imagePath, columns), resizedImageWidth);

        FileService.deleteFolder(storeImageFolder.getAbsolutePath());
    }
}
