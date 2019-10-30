package ch.uzh.marugoto.shell.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.marugoto.core.Constants;

abstract public class FileHelper {
	private static String rootFolder;
	public static final String JSON_EXTENSION = ".json";


	private final static ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).disable(MapperFeature.USE_ANNOTATIONS)
			.disable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
			.setVisibility(new ObjectMapper().getSerializationConfig().getDefaultVisibilityChecker()
					.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
					.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
					.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
					.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
					.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

	/**
	 * Get object mapper for properly reading/writing json files
	 *
	 * @return mapper
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}

	public static String getRootFolder() {
		return rootFolder;
	}

	public static void setRootFolder(String filePath) {
		if (filePath.endsWith("/")) {
			rootFolder = filePath;
		} else {
			rootFolder = filePath + "/";
		}
	}

	/**
	 * Returns all files that are not hidden from folder
	 *
	 * @param pathToDirectory
	 * @return files
	 */
	public static File[] getAllFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		return folder.listFiles(file -> !file.isHidden() && !file.isDirectory());
	}

	/**
	 * Returns all files including hidden s
	 *
	 * @param pathToDirectory
	 * @return files
	 */
	public static File[] getAllDirectories(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		return folder.listFiles(file -> file.isDirectory());
	}

	/**
	 * Returns all not hidden directories
	 *
	 * @param pathToFolder
	 * @return folders
	 */
	public static File[] getAllSubFolders(String pathToFolder) {
		File folder = new File(pathToFolder);
		var dirs = folder.listFiles(file -> !file.isHidden() && file.isDirectory());
		Arrays.sort(dirs);
		return dirs;
	}

	/**
	 * Create object from json file
	 *
	 * @param file
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static Object generateObjectFromJsonFile(File file, Class<?> cls) throws IOException {
		ObjectMapper mapper = getMapper();
		// @From and @To annotation problem
//        if (cls.equals(PageTransition.class)) {
//            mapper.disable(MapperFeature.USE_ANNOTATIONS);
//        }
        return mapper.readValue(file, cls);
    }

	/**
	 * Generate multiple json files from provided object
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
	 * Create/update json file and writes it to destination folder
	 *
	 *
	 * @param object
	 * @param fileName
	 * @param destinationFolder
	 */
	public static void generateJsonFileFromObject(Object object, String fileName, File destinationFolder) {
		var jsonPath = destinationFolder.getPath() + File.separator + fileName + JSON_EXTENSION;
		generateJsonFileFromObject(object, jsonPath);
	}

	/**
	 * Create/update json file
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

	/**
	 * Updates value in json node object
	 *
	 * @param jsonNode
	 * @param key
	 * @param value
	 */
	public static void updateReferenceValue(JsonNode jsonNode, String key, Object value) {
		((ObjectNode) jsonNode).replace(key, getMapper().convertValue(value, JsonNode.class));
	}

	/**
	 * Updates value in json file
	 *
	 * @param jsonNode
	 * @param key
	 * @param value
	 * @param jsonFile
	 */
	public static void updateReferenceValueInJsonFile(JsonNode jsonNode, String key, Object value, File jsonFile) {
		updateReferenceValue(jsonNode, key, value);
		FileHelper.generateJsonFileFromObject(jsonNode, jsonFile.getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	public static void updateIdInJsonFile(File jsonFile, String idValue) {
		try {
			var jsonObject = mapper.readValue(jsonFile, JSONObject.class);
			jsonObject.put("id", idValue);
			generateJsonFileFromObject(jsonObject, jsonFile.getAbsolutePath());
		} catch (Exception e) {
			System.out.format("Error while update id in json file '%s'", jsonFile.getAbsolutePath());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Get absolute path to json file from reference path
	 *
	 * @param relativePath
	 * @return
	 */
	public static File getJsonFileByReference(String relativePath) {
		return Paths.get(getRootFolder() + File.separator + relativePath).toFile();
	}

	/**
	 * Get relative path of json file
	 *
	 * @param file
	 * @return
	 */
	public static String getJsonFileRelativePath(File file) {
		return getJsonFileRelativePath(file.getAbsolutePath());
	}

	/**
	 * Get relative path from absolute path
	 *
	 * @param filePath
	 * @return
	 */
	public static String getJsonFileRelativePath(String filePath) {
		return filePath.replace(getRootFolder(), "");
	}

	/**
	 * Creates new folder
	 *
	 * @param folderName
	 * @return
	 */
	public static boolean generateFolder(String folderName) {
		var folderReady = false;
		File folder = new File(folderName);

		if (folder.exists() == false) {
			folderReady = folder.mkdir();
		}

		return folderReady;
	}

	/**
	 * Check if hidden folder exist, if not creates it
	 * 
	 * @param pathToFolder
	 * @return
	 */
	public static boolean hiddenFolderExist(String pathToFolder, String importerId) {

		boolean folderExist = false;
		String parentDirectory = new File(pathToFolder).getParent();

		for (File file : FileHelper.getAllDirectories(parentDirectory)) {
			if (file.getName().contains(importerId)) {
				folderExist = true;
				break;
			}
		}
		return folderExist;
	}
	
	public static String getPathToImporterFolder(String pathToFolder, String importerId) {
		return new File(pathToFolder).getParent() + File.separator + '.' + importerId;
	}

	/**
	 * Creates hidden folder, if not exist
	 *
	 * @param pathToFolder
	 * @return 
	 * @throws IOException
	 */
	public static void generateImportFolder(String pathToFolder, String importerId) {
		String hiddenFolder = getPathToImporterFolder(pathToFolder, importerId);
		generateFolder(hiddenFolder);
		// copy files from main directory to hidden directory
		copyFolder(pathToFolder, hiddenFolder);
	}

	/**
	 * Copies folder to another destination
	 *
	 * @param sourcePath
	 * @param destinationPath
	 */
	public static void copyFolder(String sourcePath, String destinationPath) {
		try {
			FileUtils.copyDirectory(new File(sourcePath), new File(destinationPath));
		} catch (IOException e) {
			System.out.format("Error coping folder '%s' to destination '%s'", sourcePath, destinationPath);
		}
	}

	/**
	 * Copy file to another location
	 *
	 * @param sourcePath
	 * @param destinationPath
	 */
	public static void copyFile(String sourcePath, String destinationPath) {
		try {
			FileUtils.copyFile(new File(sourcePath), new File(destinationPath));
		} catch (IOException e) {
			System.out.format("Error coping FILE '%s' to destination '%s'", sourcePath, destinationPath);
		}
	}

	public static void deleteFolder(String folderPath) {
		ch.uzh.marugoto.core.helpers.FileHelper.deleteFolder(folderPath);
	}

	public static String getRelativeFilePath(File relativeFrom, File targetFile) {
		var path = relativeFrom.toURI().relativize(targetFile.toURI()).getPath();
		return path;
	}

	/**
	 * Compares trees of both directories and checks if they are equal
	 * 
	 * @param pathToMainDirectory
	 * @param pathToHiddenFolder
	 * @return
	 * @throws IOException
	 */
	public static boolean compareFolders(String pathToMainDirectory, String pathToHiddenFolder) throws IOException {

		Path pathOne = Paths.get(pathToMainDirectory);
		Path pathSecond = Paths.get(pathToHiddenFolder);

		final TreeSet<String> treeOne = getAllFilePathsInFolder(pathOne);
		final TreeSet<String> treeSecond = getAllFilePathsInFolder(pathSecond);
		return treeOne.equals(treeSecond);
	}

	/**
	 * Returns all files relative path within folder
	 *
	 * @param folderPath Path
	 * @return treeSet TreeSet
	 * @throws IOException
	 */
	private static TreeSet<String> getAllFilePathsInFolder(Path folderPath) throws IOException {
		// get content of directory
		final TreeSet<String> treeSet = new TreeSet<String>();
		Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				File currentFile = new File(file.toString());
				if (!currentFile.isHidden()) {
					if (!currentFile.getAbsolutePath().contains("resources")) {
						Path relPath = folderPath.relativize(file);
						String entry = relPath.toString();
						treeSet.add(entry);
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});
		return treeSet;
	}

	public static String getObjectId(String pathToFile) {

		JSONParser parser = new JSONParser();

		String id = Constants.EMPTY_STRING;
		try {
			Object obj = parser.parse(new FileReader(pathToFile));
			JSONObject jsonObj = (JSONObject) obj;
			id = (String) jsonObj.get("id");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public static boolean isDirEmpty(String directory) throws IOException {
		boolean dirEmpty = false;

		if (getAllFiles(directory).length == 0) {
			dirEmpty = true;
		}
		return dirEmpty;
	}


}
