package ch.uzh.marugoto.shell.util;

import com.arangodb.springframework.core.ArangoOperations;
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
import java.util.Map;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.DateSolution;
import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.helpers.StringHelper;
import ch.uzh.marugoto.shell.deserializer.CriteriaDeserializer;
import ch.uzh.marugoto.shell.deserializer.DateSolutionDeserializer;
import ch.uzh.marugoto.shell.deserializer.ImageComponentDeserializer;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.helpers.JsonFileChecker;

public class BaseImport {

    private final HashMap<String, Object> objectsForImport = new HashMap<>();
    private String rootFolderPath;
    protected ObjectMapper mapper;

    public BaseImport(String pathToFolder) {
        try {
            FileHelper.setRootFolder(pathToFolder);
            mapper = FileHelper.getMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Criteria.class, new CriteriaDeserializer());
            module.addDeserializer(ImageComponent.class, new ImageComponentDeserializer());
            module.addDeserializer(DateSolution.class, new DateSolutionDeserializer());
            mapper.registerModule(module);

            rootFolderPath = pathToFolder;
            prepareObjectsForImport(pathToFolder);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected String getRootFolder() {
        return rootFolderPath;
    }

    /**
     * IMPORTANT
     * Should be removed in production,
     * useful for development phase
     */
    @Deprecated
    protected void truncateDatabase() {
        var dbConfig = BeanUtil.getBean(DbConfiguration.class);
        var operations = BeanUtil.getBean(ArangoOperations.class);

        System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));

        if (operations.driver().getDatabases().contains(dbConfig.database())) {
            operations.dropDatabase();
        }

        operations.driver().createDatabase(dbConfig.database());
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

        File[] directories = FileHelper.getAllDirectories(pathToDirectory);
        for (File directory : directories) {
            prepareObjectsForImport(directory.getAbsolutePath());
        }
    }

    @SuppressWarnings("unchecked")
	protected Object saveObject(Object obj, String filePath) {
//        System.out.println(String.format("Saving: %s", filePath));
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
        fileName = FileHelper.removeDigitsDotsAndWhitespacesFromFileName(FilenameUtils.getBaseName(fileName));

        if (fileName.contains("delete")) {
            fileName = fileName.replace("delete", "").replaceAll("\\s","");
        }

        Class<?> className = Class.forName("ch.uzh.marugoto.core.data.entity." + StringUtils.capitalize(fileName));
        return className;
    }


    @SuppressWarnings("rawtypes")
    protected ArangoRepository getRepository(Class clazz) {
        ArangoRepository repository;
        String[] componentsName = new String[] {"Exercise", "Component"};
        String resourcesName = "Resource";

        repository = (ArangoRepository) new Repositories(BeanUtil.getContext()).getRepositoryFor(clazz).orElse(null);

        if (repository == null) {
            if (stringContains(clazz.getName(), componentsName)) {
                repository = BeanUtil.getBean(ComponentRepository.class);
            } else if (clazz.getName().contains(resourcesName)) {
                repository = BeanUtil.getBean(ResourceRepository.class);
            }
        }

        return repository;
    }

    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    protected void checkJsonFilesForImport(String filePath) throws IOException, JsonFileReferenceValueException {
        var jsonFile = new File(filePath);

        if (filePath.contains("topic.json")) {
            JsonFileChecker.checkTopicJson(jsonFile);
        } else if (filePath.contains("page.json")) {
            JsonFileChecker.checkPageJson(jsonFile);
        } else if (filePath.contains("pageTransition")) {
            JsonFileChecker.checkPageTransitionJson(jsonFile);
        } else if (filePath.contains("dialogResponse")) {
            JsonFileChecker.checkDialogResponseJson(jsonFile);
        } else if (filePath.contains("notebookEntry")) {
            JsonFileChecker.checkNotebookEntryJson(jsonFile);
        } else if (StringHelper.stringContains(filePath, new String[]{"Component", "Exercise"})) {
            JsonFileChecker.checkComponentJson(jsonFile);
        }
    }

    protected void importFiles(Importer i) {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var jsonFile = new File(entry.getKey());

            try {
                importFile(jsonFile, i);
            } catch (Exception e) {
                e.printStackTrace();
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

            if (key.equals("id")) {
                continue;
            }

            i.filePropertyCheck(jsonFile, key);
            handleReferenceRelations(jsonFile, key, val, jsonNode, i);
            // handle array values
            if (val.isArray()) {
                for (JsonNode arrVal : val) {
                    if (arrVal.isObject()) {
                        var arrayIterator = arrVal.fieldNames();
                        while (arrayIterator.hasNext()) {
                            var arrayKey = arrayIterator.next();
                            handleReferenceRelations(jsonFile, arrayKey, arrVal.get(arrayKey), arrVal, i);
                        }
                    }
                }
            }
        }

        saveObjectsToDatabase(jsonFile);
        i.afterImport(jsonFile);
    }

    /**
     * Handles reference relations in json file
     * and updates reference value with saved object
     *
     * @param jsonFile
     * @param key
     * @param val
     * @param jsonNode
     * @throws Exception
     */
    private void handleReferenceRelations(File jsonFile, String key, JsonNode val, JsonNode jsonNode, Importer i) throws Exception {
        if (val.isTextual() && val.asText().contains(FileHelper.JSON_EXTENSION)) {
            var referenceFile = FileHelper.getJsonFileByReference(val.asText());
            i.referenceFileFound(jsonFile, key, referenceFile);
            importFile(referenceFile, i);
            var savedObj = getObjectsForImport().get(referenceFile.getAbsolutePath());
            FileHelper.updateReferenceValueInJsonFile(jsonNode, key, savedObj, jsonFile);
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
