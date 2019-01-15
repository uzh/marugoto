package ch.uzh.marugoto.core.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.helpers.FileHelper;

@Service
public class FileService {
	@Value("${upload.dir}")
	private String uploadDir;

	/**
	 * Return path to upload directory
	 * @return
	 */
	public String getUploadDir() {
		File folder = FileHelper.generateFolder(System.getProperty(uploadDir), Constants.UPLOAD_DIR_NAME);
		return folder.getAbsolutePath();
	}

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
			Path fileLocation = Paths.get(getUploadDir()).resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileLocation.toFile().getAbsolutePath();
		}
		catch (IOException ex) {
			throw new RuntimeException("Error: " + ex.getMessage());
		}
	}

	/**
	 * Renames file with provided name
	 *
	 * @param filePath
	 * @param newFileName
	 * @return
	 */
	public String renameFile(Path filePath, String newFileName) {
		var destination = filePath.getParent().toFile().getAbsolutePath();
		var newFilePath = destination + File.separator + newFileName + "." + FilenameUtils.getExtension(filePath.getFileName().toString());
		try {
			Files.move(filePath, Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e);
        }
		
		return newFilePath;
	}
}
