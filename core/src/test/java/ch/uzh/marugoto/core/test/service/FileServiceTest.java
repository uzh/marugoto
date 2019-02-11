package ch.uzh.marugoto.core.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.helpers.FileHelper;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class FileServiceTest extends BaseCoreTest {

    @Autowired
    private FileService fileService;
    private InputStream inputStream;
    private MockMultipartFile file;

    @Before
    public void init() throws IOException {
        super.before();
        inputStream = new URL("https://picsum.photos/600").openStream();
        file = new MockMultipartFile("file", "image.jpg", "image/jpeg", inputStream);
    }

    @Test
    public void testUploadRenameCopyAndDeleteFile() throws IOException {
        var uploadDestination = System.getProperty("user.home");

        // upload file
        var uploadedFilePath = fileService.uploadFile(Paths.get(uploadDestination), file);
        assertTrue(uploadedFilePath.toString().contains(uploadDestination));

        // rename file
        var newFileName = "junit-test";
        uploadedFilePath = fileService.renameFile(uploadedFilePath, newFileName);
        assertTrue(uploadedFilePath.toString().contains(newFileName));

        // copy file
        var newDestination = Paths.get(uploadDestination + File.separator + "copied");
        FileService.copyFile(uploadedFilePath, newDestination);
        assertTrue(newDestination.toFile().isDirectory());
        assertEquals(1, newDestination.toFile().listFiles().length);

        // delete file
        fileService.deleteFile(uploadedFilePath);
        assertFalse(uploadedFilePath.toFile().exists());

        // delete copied folder with file
        FileHelper.deleteFolder(newDestination.toString());
        assertFalse(newDestination.toFile().exists());
    }
}
