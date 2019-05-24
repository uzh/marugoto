package ch.uzh.marugoto.shell.helpers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.exception.ResizeImageException;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.Constants;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;
import ch.uzh.marugoto.shell.util.BeanUtil;

abstract public class JsonFileChecker {

    private static final ObjectMapper mapper = FileHelper.getMapper();

    
    /**
     * Check chapter json file
     * 
     * @param jsonFile
     * @throws JsonFileReferenceValueException 
     */
    public static void checkChapterJson(File jsonFile) throws JsonFileReferenceValueException {
    	handleResourcePath(jsonFile, 6);
    }
    
    /**
     * Checks page json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkPageJson(File jsonFile) throws IOException {
        var pageFolder = jsonFile.getParentFile();
        var chapterFolder = pageFolder.getParentFile();

        JsonNode jsonNode = mapper.readTree(jsonFile);
        var chapterValue = jsonNode.get("chapter");

        if (chapterValue.isNull()) {
            var chapterFilePath = FileHelper.getJsonFileRelativePath(chapterFolder) + File.separator + "chapter" + FileHelper.JSON_EXTENSION;
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "chapter", chapterFilePath, jsonFile);
        }
    }

    /**
     * Check notebookEntry json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkNotebookEntryJson(File jsonFile) throws JsonFileReferenceValueException {
        handleResourcePath(jsonFile, 6);
    }

    /**
     * Checks any component or exercise json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkComponentJson(File jsonFile) throws IOException, JsonFileReferenceValueException {
    	addPageRelation(jsonFile);
        JsonNode jsonNode = mapper.readTree(jsonFile);
        var numberOfColumns = jsonNode.get("numberOfColumns").asInt();
         
        if (jsonNode.has("zoomable") && jsonNode.get("zoomable").asBoolean()) {
        	numberOfColumns = ch.uzh.marugoto.core.Constants.IMAGE_MAX_COLUMN_WIDTH;   
        }
        handleResourcePath(jsonFile, numberOfColumns);
    }

    public static void checkCharacterJson(File jsonFile) throws JsonFileReferenceValueException {
        handleResourcePath(jsonFile, 6);
    }

    /**
     * Handles resource path in json file
     * 
     * @param jsonFile
     * @throws JsonFileReferenceValueException
     */
    public static void handleResourcePath(File jsonFile, @Nullable Integer numberOfColumns) throws JsonFileReferenceValueException {
        try {
            JsonNode jsonNode = mapper.readTree(jsonFile);

            for (var resourcePropertyName : Constants.RESOURCE_PROPERTY_NAMES) {
                var resourceNode = jsonNode.get(resourcePropertyName);

                if (jsonNode.has(resourcePropertyName) && resourceNode.isTextual()) {
                    var resourcePath = resourceNode.asText();
                    FileHelper.updateReferenceValueInJsonFile(jsonNode, resourcePropertyName, saveResourceObject(resourcePath, numberOfColumns), jsonFile);
                }
                else if (jsonNode.has(resourcePropertyName) && resourceNode.isArray()) {
                    Iterator<JsonNode> iterator = resourceNode.elements();
                    List<Object> imageResources = new ArrayList<>();

                    while (iterator.hasNext()) {
                		var resourcePath =  iterator.next().asText();
                		if (!resourcePath.isEmpty()) {                    			
                			imageResources.add(saveResourceObject(resourcePath, numberOfColumns));
                		}
                    }

                    FileHelper.updateReferenceValueInJsonFile(jsonNode, resourcePropertyName, imageResources, jsonFile);
                }
            }
        } catch (IOException | ResourceNotFoundException | ResizeImageException | ResourceTypeResolveException e) {
            throw new JsonFileReferenceValueException(e.getMessage());
        }
    }

    private static void addPageRelation(File jsonFile) throws IOException {
        JsonNode jsonNode = mapper.readTree(jsonFile);
        var pageFolder = jsonFile.getParentFile();
        var pageFilePath = FileHelper.getJsonFileRelativePath(pageFolder) + File.separator + "page" + FileHelper.JSON_EXTENSION;
        FileHelper.updateReferenceValueInJsonFile(jsonNode, "page", pageFilePath, jsonFile);
    }
    

    /**
     * Checks dialogResponse json files
     *
     * "from" and "to" values have to be manually added
     * cause it can't be guessed from folder structure
     *
     * @param jsonFile
     * @throws IOException
     * @throws JsonFileReferenceValueException
     */
    public static void checkDialogResponseJson(File jsonFile) throws IOException, JsonFileReferenceValueException {
        JsonNode jsonNode = mapper.readTree(jsonFile);
        var from = jsonNode.get("from");
        var to = jsonNode.get("to");
        var pageTransition = jsonNode.get("pageTransition");
        var valid = from.isNull() == false || to.isNull() == false;

        if (valid) {
            if (from.isTextual()) {
                valid = FileHelper.getJsonFileByReference(from.asText()).exists();
            }
            if (to.isTextual()) {
                valid = FileHelper.getJsonFileByReference(to.asText()).exists();
            }
            // optional
            if (pageTransition.isTextual()) {
                valid = FileHelper.getJsonFileByReference(pageTransition.asText()).exists();
            }
        }

        if (!valid) {
            throw new JsonFileReferenceValueException();
        }
    }

    /**
     * Checks pageTransition json file
     *
     * @param jsonFile
     * @throws IOException
     * @throws JsonFileReferenceValueException
     */
    public static void checkPageTransitionJson(File jsonFile) throws IOException, JsonFileReferenceValueException {
        var pageFolder = jsonFile.getParentFile();
        JsonNode jsonNode = mapper.readTree(jsonFile);
        var from = jsonNode.get("from");
        var to = jsonNode.get("to");
        var valid = from.isObject() && to.isObject();

        if (to.isTextual()) {
            valid = FileHelper.getJsonFileByReference(to.asText()).exists();
        }

        if (from.isTextual()) {
            valid = FileHelper.getJsonFileByReference(from.asText()).exists();
        }

        if (from.isNull()) {
            var fromPath = pageFolder.getAbsolutePath() + File.separator + "page" + FileHelper.JSON_EXTENSION;
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "from", FileHelper.getJsonFileRelativePath(fromPath), jsonFile);
        }

        if (!valid) {
            throw new JsonFileReferenceValueException();
        }
    }

    /**
     * Checks topic json file
     *
     * @param jsonFile
     * @throws IOException
     * @throws JsonFileReferenceValueException
     */
    public static void checkTopicJson(File jsonFile) throws IOException, JsonFileReferenceValueException {
        var startPage = mapper.readTree(jsonFile).get("startPage");
        var valid = startPage.isNull() == false;

        if (startPage.isTextual()) {
            valid = FileHelper.getJsonFileByReference(startPage.asText()).exists();
        }

        if (!valid) {
            throw new JsonFileReferenceValueException();
        }

        handleResourcePath(jsonFile, 12);
    }

    public static void checkNotificationJson(File jsonFile) throws IOException {
        JsonNode jsonNode = mapper.readTree(jsonFile);
        var pageValue = jsonNode.get("page");
        var timeValue = jsonNode.get("receiveAfter");
        var characterValue = jsonNode.get("from");
        var pageFolder = jsonFile.getParentFile();

        if (pageValue.isNull()) {
            var pageFilePath = FileHelper.getJsonFileRelativePath(pageFolder) + File.separator + "page" + FileHelper.JSON_EXTENSION;
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "page", FileHelper.getJsonFileRelativePath(pageFilePath), jsonFile);
        }

        if (timeValue.isTextual()) {
            var virtualTime = new VirtualTime();
            virtualTime.setTime(Duration.parse(timeValue.asText()));
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "receiveAfter", virtualTime, jsonFile);
        }

        if (characterValue.isNull()) {
            var characterFilePath = FileHelper.getJsonFileRelativePath(pageFolder) + File.separator + "character1" + FileHelper.JSON_EXTENSION;
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "from", FileHelper.getJsonFileRelativePath(characterFilePath), jsonFile);
        }
    }

    private static Object saveResourceObject(String resourcePath, @Nullable Integer numberOfColumns) throws ResourceTypeResolveException, ResourceNotFoundException, ResizeImageException, IOException {
        var resourceService = BeanUtil.getBean(ResourceService.class);
        var imageService = BeanUtil.getBean(ImageService.class);
        numberOfColumns = numberOfColumns == null ? 12 : numberOfColumns;

        if (false == resourcePath.contains(FileHelper.getRootFolder())) {
            resourcePath = FileHelper.getRootFolder() + resourcePath;
        }

        var resourceObject = ResourceFactory.getResource(resourcePath);

        if (resourceObject instanceof ImageResource) {
            resourceObject = imageService.saveImageResource(Paths.get(resourcePath), numberOfColumns);
        } else {
            resourceObject.setPath(resourcePath);
            resourceObject = resourceService.saveResource(resourceObject);
        }

        return resourceObject;
    }
}
