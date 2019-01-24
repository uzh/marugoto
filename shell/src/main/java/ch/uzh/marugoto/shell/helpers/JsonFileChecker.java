package ch.uzh.marugoto.shell.helpers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.exception.ResizeImageException;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;
import ch.uzh.marugoto.shell.util.BeanUtil;

abstract public class JsonFileChecker {

    private static final ObjectMapper mapper = FileHelper.getMapper();

    /**
     * Checks page json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkPageJson(File jsonFile) throws IOException {
        var pageFolder = jsonFile.getParentFile();
        var chapterFolder = pageFolder.getParentFile();
        var storylineFolder = chapterFolder.getParentFile();

        JsonNode jsonNode = mapper.readTree(jsonFile);
        var storylineValue = jsonNode.get("storyline");
        var chapterValue = jsonNode.get("chapter");

        if (storylineValue.isNull()) {
            var storylineFilePath = FileHelper.getJsonFileRelativePath(storylineFolder) + File.separator + "storyline" + FileHelper.JSON_EXTENSION;
            FileHelper.updateReferenceValueInJsonFile(jsonNode, "storyline", storylineFilePath, jsonFile);
        }

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
        handleResourcePath(jsonFile);
    }

    /**
     * Checks any component or exercise json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkComponentJson(File jsonFile) throws IOException, JsonFileReferenceValueException {
    	addPageRelation(jsonFile);
        handleResourcePath(jsonFile);
    }

    public static void checkCharacterJson(File jsonFile) throws JsonFileReferenceValueException {
        handleResourcePath(jsonFile);
    }

    /**
     * Handles resource path in json file
     * 
     * @param jsonFile
     * @throws JsonFileReferenceValueException
     */
    public static void handleResourcePath(File jsonFile) throws JsonFileReferenceValueException {
        try {
            JsonNode jsonNode = mapper.readTree(jsonFile);

            for (var resourcePropertyName : ResourceFactory.getResourceTypes()) {
                var resourceNode = jsonNode.get(resourcePropertyName);
                if (jsonNode.has(resourcePropertyName) && resourceNode.isTextual()) {
                    var resourcePath = resourceNode.asText();

                    if (resourcePath.contains(FileHelper.getRootFolder()) == false) {
                        resourcePath = FileHelper.getRootFolder() + resourcePath;
                    }

                    var resourceObject = ResourceFactory.getResource(resourcePropertyName);

                    if (resourceObject instanceof ImageResource) {
                        var imageService = BeanUtil.getBean(ImageService.class);
                        resourceObject = imageService.saveImageResource(Paths.get(resourcePath));
                    } else {
                        var resourceService = BeanUtil.getBean(ResourceService.class);
                        resourceObject.setPath(resourcePath);
                        resourceObject = resourceService.saveResource(resourceObject);
                    }

                    FileHelper.updateReferenceValueInJsonFile(jsonNode, resourcePropertyName, resourceObject, jsonFile);
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
    }
}
