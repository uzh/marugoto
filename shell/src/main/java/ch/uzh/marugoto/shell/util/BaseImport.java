package ch.uzh.marugoto.shell.util;

import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.io.FilenameUtils;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.DateSolution;
import ch.uzh.marugoto.core.data.entity.topic.Resource;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.helpers.StringHelper;
import ch.uzh.marugoto.shell.deserializer.CriteriaDeserializer;
import ch.uzh.marugoto.shell.deserializer.DateSolutionDeserializer;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.helpers.JsonFileChecker;

public class BaseImport {

    private final HashMap<String, Object> objectsForImport = new HashMap<>();
    private String rootFolderPath;
    protected ObjectMapper mapper;
    private Stack<Object> savingQueue = new Stack<>();

    public BaseImport(String pathToFolder) {
        try {
            FileHelper.setRootFolder(pathToFolder);
            mapper = FileHelper.getMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Criteria.class, new CriteriaDeserializer());
            module.addDeserializer(DateSolution.class, new DateSolutionDeserializer());
            mapper.registerModule(module);

            rootFolderPath = pathToFolder;
            prepareObjectsForImport(pathToFolder);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected String getRootFolder() {
        return rootFolderPath;
    }

    protected HashMap<String, Object> getObjectsForImport() {
        return objectsForImport;
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

    @SuppressWarnings("unchecked")
	protected Object saveObject(Object obj, String filePath) {
        var savedObject = getRepository(obj.getClass()).save(obj);
       // update json file
        FileHelper.generateJsonFileFromObject(savedObject, filePath);
        return savedObject;
    }

    protected Object getObjectId(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field id;
        if (obj instanceof Component) {
            id = getEntityClassByName("Component").getDeclaredField("id");
        } else if (obj instanceof Resource) {
            id = getEntityClassByName("Resource").getDeclaredField("id");
        } else {
            id = obj.getClass().getDeclaredField("id");
        }

        if (id != null) {
            id.setAccessible(true);
        }

        return id.get(obj);
    }

    protected Class<?> getEntityClassByName(String fileName) throws ClassNotFoundException {
        fileName = StringHelper.removeNumbers(FilenameUtils.getBaseName(fileName));

        if (fileName.contains("delete")) {
            fileName = fileName.replace("delete", "").replaceAll("\\s","");
        }

        Class<?> className = Class.forName("ch.uzh.marugoto.core.data.entity.topic." + StringUtils.capitalize(fileName));
        return className;
    }


    @SuppressWarnings("rawtypes")
    protected ArangoRepository getRepository(Class clazz) {
        ArangoRepository repository;
        String[] componentsName = new String[] {"Exercise", "Component"};
        String[] notificationsName = new String[] {"Mail", "Dialog"};
        String resourcesName = "Resource";
        String entryName = "NotebookEntry";

        repository = (ArangoRepository) new Repositories(BeanUtil.getContext()).getRepositoryFor(clazz).orElse(null);

        if (repository == null) {
            if (stringContains(clazz.getName(), componentsName)) {
                repository = BeanUtil.getBean(ComponentRepository.class);
	        } else if (stringContains(clazz.getName(), notificationsName)) {
                repository = BeanUtil.getBean(NotificationRepository.class);
            } else if (clazz.getName().contains(resourcesName)) {
                repository = BeanUtil.getBean(ResourceRepository.class);
	        } else if (clazz.getName().contains(entryName)) {
	            repository = BeanUtil.getBean(NotebookEntryRepository.class);
            }
        }

        if (repository == null) {
            throw new RuntimeException("Repository not found!");
        }

        return repository;
    }

    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    protected void checkJsonFilesForImport(String filePath) throws JsonFileReferenceValueException, IOException {
        var jsonFile = new File(filePath);

        if (filePath.contains("topic.json")) {
            JsonFileChecker.checkTopicJson(jsonFile);
        } else if (filePath.contains("page.json")) {
            JsonFileChecker.checkPageJson(jsonFile);
        } else if (filePath.contains("pageTransition")) {
            JsonFileChecker.checkPageTransitionJson(jsonFile);
        } else if (filePath.contains("dialogResponse")) {
            JsonFileChecker.checkDialogResponseJson(jsonFile);
        } else if (StringHelper.stringContains(filePath, new String[]{"notebookEntry", "NotebookEntry"})) {
            JsonFileChecker.checkNotebookEntryJson(jsonFile);
        } else if (StringHelper.stringContains(filePath, new String[]{"Component", "Exercise"})) {
            JsonFileChecker.checkComponentJson(jsonFile);
        } else if (StringHelper.stringContains(StringHelper.removeNumbers(filePath), new String[]{"mail.json", "dialog.json"})) {
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
    protected void importFiles(Importer i) {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var jsonFile = new File(entry.getKey());
            System.out.println("path"+ jsonFile.getAbsolutePath());
            try {
                importFile(jsonFile, i);
            } catch (Exception e) {
                throw new RuntimeException("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Checks values in json files for reference relations (relations to another files)
     *
     * @param jsonFile
     * @throws Exception
     */
    protected void importFile(File jsonFile, Importer i) throws Exception {
        var jsonNode = mapper.readTree(jsonFile);
        var iterator = jsonNode.fieldNames();

        while (iterator.hasNext()) {
            var key = iterator.next();
            var val = jsonNode.get(key);

            i.filePropertyCheck(jsonFile, key);

            if (val.isTextual() && val.asText().contains(FileHelper.JSON_EXTENSION)) {
                var savedReferenceObject = handleReferenceRelations(jsonFile, key, val, i);
                FileHelper.updateReferenceValueInJsonFile(jsonNode, key, savedReferenceObject, jsonFile);
            } else if (val.isArray()) {
                handleReferenceInArray(jsonFile, val, i);
                FileHelper.updateReferenceValueInJsonFile(jsonNode, key, val, jsonFile);
            }
        }
        System.out.println("Saving: " + jsonFile);
        saveObjectsToDatabase(jsonFile);
        savingQueue.remove(jsonFile);
        i.afterImport(jsonFile);
    }

    /**
     * Handles reference relations in json file
     * and updates reference value with saved object
     *
     * @param jsonFile
     * @param key
     * @param val
     * @param i
     * @throws Exception
     */
    private Object handleReferenceRelations(File jsonFile, String key, JsonNode val, Importer i) throws Exception {
        var referenceFile = FileHelper.getJsonFileByReference(val.asText());
        i.referenceFileFound(jsonFile, key, referenceFile);
        savingQueue.add(jsonFile);

        if (savingQueue.contains(referenceFile) == false) {
            importFile(referenceFile, i);
        }

        return getObjectsForImport().get(referenceFile.getAbsolutePath());
    }

    private void handleReferenceInArray(File jsonFile, JsonNode val, Importer i) throws Exception {
        for (JsonNode jsonNode : val) {
            if (jsonNode.isObject()) {
                var nodeIterator = jsonNode.fieldNames();
                while (nodeIterator.hasNext()) {
                    var nodeKey = nodeIterator.next();
                    var nodeVal = jsonNode.get(nodeKey);
                    if (nodeVal.isTextual() && nodeVal.asText().contains(FileHelper.JSON_EXTENSION)) {
                        var savedReferenceObject = handleReferenceRelations(jsonFile, nodeKey, nodeVal, i);
                        FileHelper.updateReferenceValue(jsonNode, nodeKey, savedReferenceObject);
                    }
                }
            }
        }
    }

    /**
     * Saves object to DB and updates ID value in json file
     * also overrides object for import with saved one
     *
     * @param jsonFile
     * @throws IOException
     */
    private void saveObjectsToDatabase(File jsonFile) throws IOException {
        // save it
        Object obj = getObjectsForImport().get(jsonFile.getAbsolutePath());
        obj = FileHelper.generateObjectFromJsonFile(jsonFile, obj.getClass());
        obj = saveObject(obj, jsonFile.getAbsolutePath());
        getObjectsForImport().replace(jsonFile.getAbsolutePath(), obj);
    }
}
