package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class FileService {

	private String uploadDir = "user.home";

	private final static ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).disable(MapperFeature.USE_ANNOTATIONS)
			.disable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING);

	public static ObjectMapper getMapper() {
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		return mapper;
	}

	/**
	 * Generates folder from provided path
	 *
	 * @param destinationPath
	 * @return
	 */
	public static File generateFolder(String destinationPath) {
		File folder = new File(destinationPath);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		return folder;
	}

	/**
	 * Makes folder with provided name on the provided path
	 *
	 * @param destinationPath
	 * @param folderName
	 * @return
	 */
	public static File generateFolder(String destinationPath, String folderName) {
		return generateFolder(destinationPath + File.separator + folderName);
	}

	/**
	 * Generates multiple json files from provided object
	 *
	 * @param object
	 * @param fileName
	 * @param destinationFolder
	 * @param numOfFiles
	 */
	public static void generateInitialJsonFilesFromObject(Object object, String fileName, File destinationFolder,
			int numOfFiles) {
		for (int i = 1; i <= numOfFiles; i++) {
			var jsonFileName = fileName + i;
			generateJsonFileFromObject(object, jsonFileName, destinationFolder);
		}
	}

	/**
	 * Generates json file from provided object
	 *
	 * @param object
	 * @param fileName
	 * @param destinationFolder
	 */
	public static void generateJsonFileFromObject(Object object, String fileName, File destinationFolder) {
		var json = new File(destinationFolder.getPath() + File.separator + fileName + ".json");

		try {
			getMapper().writeValue(json, object);
			System.out.println(fileName + " added to " + destinationFolder.getName() + " (total "
					+ Objects.requireNonNull(destinationFolder.listFiles()).length + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create or override existing json file with Object
	 *
	 * @param object
	 * @param pathToFile
	 */
	public static void generateJsonFileFromObject(Object object, String pathToFile) {
		try {
			getMapper().writeValue(new File(pathToFile), object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object generateObjectFromJsonFile(File file, Class<?> cls) throws IOException {
		return getMapper().readValue(file, cls);
	}

	/**
	 *
	 * @param pathToDirectory
	 * @return
	 */
	public static File[] getAllFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		return folder.listFiles(file -> !file.isHidden() && !file.isDirectory());
	}

	public static File[] getAllDirectories(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		return folder.listFiles(file -> !file.isHidden() && file.isDirectory());
	}

	public static void deleteFolder(String pathToDirectory) {
		File folder = new File(pathToDirectory);

		for (var file : folder.listFiles()) {
			file.delete();
		}

		folder.delete();
	}

	public String getUploadDir() {
		File folder = generateFolder(System.getProperty(uploadDir), "uploads");
		return folder.getAbsolutePath();
	}

	public void renameFile(String filePath, String newFileName) throws Exception {
		File oldFile = new File(filePath);
		File newFile = new File(getUploadDir() + File.separator + newFileName);
		try {
			oldFile.renameTo(newFile);
		} catch (Exception e) {
			throw new Exception("File can not be renamed");
		}
	}

	public static String getFileNameWithoutExtension(File file) {
		String name = file.getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			name = name.substring(0, pos);
		}
		return name;
	}
}
