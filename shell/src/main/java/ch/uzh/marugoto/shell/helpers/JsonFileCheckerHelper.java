package ch.uzh.marugoto.shell.helpers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.shell.exceptions.JsonFileReferenceValueException;

public class JsonFileCheckerHelper extends FileHelper {

    private static final ObjectMapper mapper = getMapper();

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
            var storylineFilePath = getJsonFileRelativePath(storylineFolder) + File.separator + "storyline" + JSON_EXTENSION;
            updateReferenceValueInJsonFile(jsonNode, "storyline", storylineFilePath, jsonFile);
        }

        if (chapterValue.isNull()) {
            var chapterFilePath = getJsonFileRelativePath(chapterFolder) + File.separator + "chapter" + JSON_EXTENSION;
            updateReferenceValueInJsonFile(jsonNode, "chapter", chapterFilePath, jsonFile);
        }
    }

    /**
     * Check notebookEntry json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkNotebookEntryJson(File jsonFile) throws IOException {
        var pageFolder = jsonFile.getParentFile();
        var pageFilePath = getJsonFileRelativePath(pageFolder) + File.separator + "page" + JSON_EXTENSION;
        updateReferenceValueInJsonFile(mapper.readTree(jsonFile), "page", pageFilePath, jsonFile);
    }

    /**
     * Checks any component or exercise json file
     *
     * @param jsonFile
     * @throws IOException
     */
    public static void checkComponentJson(File jsonFile) throws IOException {
        var pageFolder = jsonFile.getParentFile();
        var pageFilePath = getJsonFileRelativePath(pageFolder) + File.separator + "page" + JSON_EXTENSION;
        updateReferenceValueInJsonFile(mapper.readTree(jsonFile), "page", pageFilePath, jsonFile);
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
                valid = getJsonFileByReference(from.asText()).exists();
            }
            if (to.isTextual()) {
                valid = getJsonFileByReference(to.asText()).exists();
            }
            // optional
            if (pageTransition.isTextual()) {
                valid = getJsonFileByReference(pageTransition.asText()).exists();
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
        var valid = false;

        if (to.isTextual()) {
            valid = getJsonFileByReference(to.asText()).exists();
        }

        if (from.isTextual()) {
            valid = getJsonFileByReference(from.asText()).exists();
        }

        if (from.isNull()) {
            var fromPath = pageFolder.getAbsolutePath() + File.separator + "page" + JSON_EXTENSION;
            updateReferenceValueInJsonFile(jsonNode, "from", getJsonFileRelativePath(fromPath), jsonFile);
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
            valid = getJsonFileByReference(startPage.asText()).exists();
        }

        if (!valid) {
            throw new JsonFileReferenceValueException();
        }
    }
}
