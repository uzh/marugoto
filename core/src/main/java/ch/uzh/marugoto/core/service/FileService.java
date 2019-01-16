package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.helpers.FileHelper;

@Service
public class FileService {

	/**
	 * Copy file to destination
	 *
	 * @param sourcePath
	 * @param destinationPath
	 * @return
	 */
	public static void copyFile(Path sourcePath, Path destinationPath) throws IOException {
		if (destinationPath.toFile().exists() == false) {
			FileHelper.generateFolder(destinationPath.toAbsolutePath().toString());
		}

		var destinationFilePath = destinationPath.resolve(sourcePath.getFileName());
		Files.copy(sourcePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Upload file to upload directory
	 *
	 * @param file
	 */
	public String uploadFile(MultipartFile file) {
		try {
			Path fileLocation = Paths.get(UploadExerciseService.getUploadDirectory()).resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileLocation.toFile().getAbsolutePath();
		}
		catch (IOException ex) {
			throw new RuntimeException("Error: " + ex.getMessage());
		}
	}

	/**
	 * @param filePath
	 * @param newFileName
	 * 
	 * @return newFilePath
	 */
	public String renameFile(Path filePath, String newFileName) {
		var destination = filePath.getParent().toFile().getAbsolutePath();
		var newName = newFileName + "." + FilenameUtils.getExtension(filePath.getFileName().toString());
		var newFilePath = destination + File.separator + newName;
		try {
			Files.move(filePath, Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e);
        }
		
		return newName;
	}
	
	public void deleteFile (Path filePath) throws IOException {
		if (filePath.toFile().exists()) {
			Files.delete(filePath);	
		}
	}
}



