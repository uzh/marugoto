package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.helpers.FileHelper;

@Service
public class FileService {

	@Value("${marugoto.resource.temp.dir}")
	protected String tempDirectory;

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
	public Path uploadFile(Path destination, MultipartFile file) {
		try {
			Path fileLocation = destination.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileLocation;
		}
		catch (IOException ex) {
			throw new RuntimeException("Error: " + ex.getMessage());
		}
	}

	/**
	 * Renames file with provided name
	 *
	 * @param filePath
	 * @param newName
	 * 
	 * @return newFilePath
	 */
	public Path renameFile(Path filePath, String newName) {
		var destination = filePath.getParent().toFile().getAbsolutePath();
		var newFileName = newName + "." + FilenameUtils.getExtension(filePath.getFileName().toString());
		var newFilePath = destination + File.separator + newFileName;
		try {
			Files.move(filePath, Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e);
        }
		
		return Paths.get(newFilePath);
	}

	/**
	 * Deletes file
	 *
	 * @param filePath
	 * @throws IOException
	 */
	public void deleteFile (Path filePath) throws IOException {
		if (filePath.toFile().exists()) {
			Files.delete(filePath);	
		}
	}

	/**
	 * Created zip file from name and input stream map
	 *
	 * @param nameAndInputStreamMap
	 * @param zipName
	 * @return
	 * @throws CreateZipException
	 */
	public FileInputStream zipMultipleInputStreams(HashMap<String, InputStream> nameAndInputStreamMap, String zipName) throws CreateZipException {
		try {
			var zipPath = tempDirectory + File.separator + zipName + Constants.ZIP_EXTENSION;
			FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

			for (String fileName : nameAndInputStreamMap.keySet()) {
				InputStream fileInputStream = nameAndInputStreamMap.get(fileName);
				if(!fileName.contains("file")) {
					fileName = fileName + Constants.PDF_EXTENSION;
				}
				zipOutputStream.putNextEntry(new ZipEntry(fileName));
				byte[] bytes = new byte[fileInputStream.available()];
				int length;
				while((length = fileInputStream.read(bytes)) >= 0) {
					zipOutputStream.write(bytes, 0, length);
				}

				fileInputStream.close();
			}

			zipOutputStream.close();
			fileOutputStream.close();

			FileInputStream inputStream = new FileInputStream(Paths.get(zipPath).toFile());
			deleteFile(Paths.get(zipPath));

			return inputStream;
		} catch (IOException e) {
			throw new CreateZipException(e.getMessage());
		}
	}
}



