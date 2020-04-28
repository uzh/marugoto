package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.DateSolution;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Resource;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.helpers.StringHelper;
import ch.uzh.marugoto.shell.deserializer.CriteriaDeserializer;
import ch.uzh.marugoto.shell.deserializer.DateSolutionDeserializer;
import ch.uzh.marugoto.shell.deserializer.ResourceDeserializer;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.helpers.JsonFileChecker;
import ch.uzh.marugoto.shell.helpers.RepositoryHelper;

public class Importer {

	private final HashMap<String, Object> objectsForImport = new HashMap<>();
	private Stack<Object> savingQueue = new Stack<>();
	protected String hiddenFolderPath;
	protected String originalFolderPath;
	protected String updatedFolderPath;
	protected ObjectMapper mapper;

	public Importer(String path, String importerId) {
		try {
			originalFolderPath = path;
			hiddenFolderPath = FileHelper.getPathToImporterFolder(path, importerId);
			FileHelper.setRootFolder(hiddenFolderPath);

			mapper = FileHelper.getMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(Criteria.class, new CriteriaDeserializer());
			module.addDeserializer(DateSolution.class, new DateSolutionDeserializer());
			module.addDeserializer(Resource.class, new ResourceDeserializer());
			mapper.registerModule(module);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void doImport() throws Exception {
		prepareObjectsForImport(hiddenFolderPath);
		importFiles();
	}

	/**
	 * Import data from generated folder structure
	 *
	 * @param pathToDirectory
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected void prepareObjectsForImport(String pathToDirectory) throws Exception {
		for (var file : FileHelper.getAllFiles(pathToDirectory)) {
			var filePath = file.getPath();
			System.out.println("Preparing:" + filePath);

			try {
				checkJsonFilesForImport(filePath);
			} catch (JsonFileReferenceValueException e) {
				throw new Exception("Reference is not valid in JSON: " + filePath);
			}

			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			objectsForImport.put(filePath, obj);
		}

		File[] directories = FileHelper.getAllSubFolders(pathToDirectory);
		for (File directory : directories) {
			if (directory.getName().contains("resources")) {
				continue;
			}
			prepareObjectsForImport(directory.getAbsolutePath());
		}
	}

	protected Class<?> getEntityClassByName(String fileName) throws ClassNotFoundException {
		fileName = StringHelper.removeNumbers(FilenameUtils.getBaseName(fileName));

		if (fileName.contains("delete")) {
			fileName = fileName.replace("delete", "").replaceAll("\\s", "");
		}

		Class<?> className = Class
				.forName("ch.uzh.marugoto.core.data.entity.topic." + StringUtils.capitalize(fileName));
		return className;
	}

	protected void checkJsonFilesForImport(String filePath) throws JsonFileReferenceValueException, IOException {
		var jsonFile = new File(filePath);

		if (filePath.contains("topic.json")) {
			JsonFileChecker.checkTopicJson(jsonFile);
		} else if (filePath.contains("chapter.json")) {
			JsonFileChecker.checkChapterJson(jsonFile);
		} else if (filePath.contains("page.json")) {
			JsonFileChecker.checkPageJson(jsonFile);
		} else if (filePath.contains("pageTransition")) {
			JsonFileChecker.checkPageTransitionJson(jsonFile);
		} else if (filePath.contains("dialogResponse")) {
			JsonFileChecker.checkDialogResponseJson(jsonFile);
		} else if (StringHelper.stringContains(filePath, new String[] { "notebookEntry", "NotebookEntry" })) {
			JsonFileChecker.checkNotebookEntryJson(jsonFile);
		} else if (StringHelper.stringContains(filePath, new String[] { "Component", "Exercise" })) {
			JsonFileChecker.checkComponentJson(jsonFile);
		} else if (StringHelper.stringContains(StringHelper.removeNumbers(filePath),
				new String[] { "mail.json", "dialog.json" })) {
			JsonFileChecker.checkNotificationJson(jsonFile);
		} else if (filePath.contains("character")) {
			JsonFileChecker.checkCharacterJson(jsonFile);
		}
	}
	
	/**
	 * Import files from list
	 *
	 * @param i Importer
	 */
	protected void importFiles() {
		for (Map.Entry<String, Object> entry : objectsForImport.entrySet()) {
			var jsonFile = new File(entry.getKey());
			System.out.println("Path: " + jsonFile.getAbsolutePath());
			try {
				importFile(jsonFile);
			} catch (Exception e) {
				throw new RuntimeException("ERROR: " + e.getMessage());
			}
		}
	}

	/**
	 * Importing files
	 *
	 * @param jsonFile
	 * @throws Exception
	 */
	protected void importFile(File jsonFile) throws Exception {
		checkFilePropertiesAndReferences(jsonFile);
		System.out.println("Saving: " + jsonFile);
		saveObjectsToDatabase(jsonFile);
		savingQueue.remove(jsonFile);
		System.out.println("Saved: " + jsonFile);
	}


	/**
	 * Checks values in json files for reference relations (relations to another
	 * files)
	 *
	 * @param jsonFile
	 * @throws Exception
	 */
	protected void checkFilePropertiesAndReferences(File jsonFile) throws Exception {
		var jsonNode = mapper.readTree(jsonFile);
		var iterator = jsonNode.fieldNames();

		while (iterator.hasNext()) {
			var key = iterator.next();
			var val = jsonNode.get(key);

			if (val.isTextual() && val.asText().contains(FileHelper.JSON_EXTENSION)) {
				var savedReferenceObject = handleReferenceRelations(jsonFile, key, val);
				if (savedReferenceObject != null) {
					FileHelper.updateReferenceValueInJsonFile(jsonNode, key, savedReferenceObject, jsonFile);
				}
			} else if (val.isArray()) {
				handleReferenceInArray(jsonFile, val);
				FileHelper.updateReferenceValueInJsonFile(jsonNode, key, val, jsonFile);
			}
		}

	}

	/**
	 * Handles reference relations in json file and updates reference value with
	 * saved object
	 *
	 * @param jsonFile
	 * @param key
	 * @param val
	 * @param i
	 * @throws Exception
	 */
	private Object handleReferenceRelations(File jsonFile, String key, JsonNode val) throws Exception {
		Object handledObject = null;
		var referenceFile = FileHelper.getJsonFileByReference(val.asText());

		if (referenceFile.exists()) {
			referenceFileFound(jsonFile, key, referenceFile);
			var isKeyReferenceToFile = JsonFileChecker.isPropertyReferenceToFile(key);
			if (isKeyReferenceToFile) {
				if (savingQueue.contains(referenceFile) == false) {
					savingQueue.add(referenceFile);
					importFile(referenceFile);
				}
				handledObject = objectsForImport.get(referenceFile.getAbsolutePath());
			}			
		}
		return handledObject;
	}

	/**
	 * Handles array in json file with values as reference relations
	 *
	 * @param jsonFile
	 * @param val
	 * @param i
	 * @throws Exception
	 */
	private void handleReferenceInArray(File jsonFile, JsonNode val) throws Exception {
		for (JsonNode jsonNode : val) {
			if (jsonNode.isObject()) {
				var nodeIterator = jsonNode.fieldNames();
				while (nodeIterator.hasNext()) {
					var nodeKey = nodeIterator.next();
					var nodeVal = jsonNode.get(nodeKey);
					if (nodeVal.isTextual() && nodeVal.asText().contains(FileHelper.JSON_EXTENSION)) {
						var savedReferenceObject = handleReferenceRelations(jsonFile, nodeKey, nodeVal);
						if (savedReferenceObject != null) {
							FileHelper.updateReferenceValue(jsonNode, nodeKey, savedReferenceObject);
						}
					}
					else if (nodeVal.isArray()) {
                    	List<Object> affectedPageIds = new ArrayList<>();
                        Iterator<JsonNode> iterator = nodeVal.elements();

                        while (iterator.hasNext()) {
                        	var iteratorVal = iterator.next();

                        	if (iteratorVal.asText().contains(FileHelper.JSON_EXTENSION)) {
                        		var savedObj = (Page)handleReferenceRelations(jsonFile, nodeKey, iteratorVal);
                        		if (savedObj != null) {
                        			affectedPageIds.add(savedObj.getId());
                        		}
							} else {
								affectedPageIds.add(iteratorVal);
							}
                        }
                        FileHelper.updateReferenceValue(jsonNode, nodeKey, affectedPageIds);
                    }
				}
			}
		}
	}

	public void referenceFileFound(File jsonFile, String key, File referenceFile) {
		System.out.println(String.format("Reference found (%s): %s in file %s", key, referenceFile.getAbsolutePath(), jsonFile));
	}

	/**
	 * Saves object to DB and updates ID value in json file also overrides object
	 * for import with saved one
	 *
	 * @param jsonFile
	 * @throws IOException
	 */
	private void saveObjectsToDatabase(File jsonFile) throws IOException {
		// save it
		Object obj = objectsForImport.get(jsonFile.getAbsolutePath());
		obj = FileHelper.generateObjectFromJsonFile(jsonFile, obj.getClass());
		
		//add file name to page title!
		//uncomment these lines if you want to have the file numbers at the page title!
//		if(obj instanceof Page && jsonFile.getName().endsWith("page.json")) {
//			try {
//				Page p = (Page)obj;
//				String pagePath = jsonFile.getParent();
//				
//				String fileNameToAdd = "-" + jsonFile.getParent().substring(pagePath.lastIndexOf("/")+1, jsonFile.getParent().length());
//				
//				if(p.getTitle().contains(fileNameToAdd) == false) {
//					p.setTitle(p.getTitle() + fileNameToAdd);
//				}
//			} catch (Exception e) {}
//		}
		
		if(obj instanceof PageTransition) {
			int endIndex = jsonFile.getName().length()-5;
			((PageTransition) obj).setRenderOrder(Integer.parseInt(jsonFile.getName().subSequence(14, endIndex).toString()));
		}
		obj = saveObject(obj, jsonFile.getAbsolutePath());
		objectsForImport.replace(jsonFile.getAbsolutePath(), obj);
	}

	
	private Object saveObject(Object obj, String filePath) {
		var savedObject = saveObject(obj);
		// update json file
		FileHelper.generateJsonFileFromObject(savedObject, filePath);
		return savedObject;
	}

	@SuppressWarnings("unchecked")
	protected Object saveObject(Object obj) {
		return RepositoryHelper.getRepository(obj.getClass()).save(obj);
	}
	
	public void deleteTopic() throws Exception {
		for (var file : FileHelper.getAllFiles(hiddenFolderPath)) {
			removeFile(file);
		}

		for (File directory : FileHelper.getAllSubFolders(hiddenFolderPath)) {
			removeFolder(directory);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFile(File file) throws Exception {

		if (file.getAbsolutePath().contains("resource") == false) {
			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			var repo = RepositoryHelper.getRepository(obj.getClass());
			try {
				var id = FileHelper.getObjectId(file.getAbsolutePath());
				if (repo != null && !id.isEmpty()) {
					repo.deleteById(id);
				}
			} catch (Exception e) {
				throw new RuntimeException("Get object ID error :" + obj.getClass().getName());
			}
		}

		file.delete();
		System.out.println("File deleted: " + file.getAbsolutePath());
	}

	private void removeFolder(File file) throws Exception {

		for (var fileForRemoval : FileHelper.getAllFiles(file.getAbsolutePath())) {
			removeFile(fileForRemoval);
		}

		for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
			removeFolder(directory);
		}

		var dirEmpty = FileHelper.isDirEmpty(file.getAbsolutePath());
		if (dirEmpty) {
			FileUtils.deleteDirectory(file);
		}
	}

	/**
	 * Find topic ID
	 *
	 * @return
	 */
	private String getTopicId() {
		File topicJson = Paths.get(hiddenFolderPath.concat("/topic.json")).toFile();
		return FileHelper.getObjectId(topicJson.getAbsolutePath());
	}

	public void removePlayerStates() throws Exception {
		var gameStates = RepositoryHelper.getGameStatesForTopic(getTopicId());
		for (GameState gameState : gameStates) {
			RepositoryHelper.deleteUserStates(gameState.getId());
			RepositoryHelper.deleteDialogStates(gameState.getId());
			RepositoryHelper.deleteMailStates(gameState.getId());
			RepositoryHelper.deleteNotebookEntryStates(gameState.getId());

			var pageStates = RepositoryHelper.findPageStatesByGameState(gameState.getId());

			if(pageStates != null) {
				for (PageState pageState : pageStates) {
					RepositoryHelper.deleteExerciseStates(pageState.getId());
				}
			}

			RepositoryHelper.deletePageStates(gameState.getId());
			RepositoryHelper.delete(gameState);
		}
	}
	
	public void deactivateTopic() throws Exception {
		File topicJson = Paths.get(hiddenFolderPath.concat("/topic.json")).toFile();
		Topic topicObj = (Topic)FileHelper.generateObjectFromJsonFile(topicJson, Topic.class);
		topicObj.setActive(false);
		saveObject(topicObj);
	}
	
	public void deleteHiddenFolder() throws Exception {
		FileUtils.deleteDirectory(new File(hiddenFolderPath));
	}
}
