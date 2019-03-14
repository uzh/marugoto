package ch.uzh.marugoto.shell.helpers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.LinkComponent;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.PdfResource;
import ch.uzh.marugoto.core.data.entity.topic.VideoResource;

abstract public class FileHelper {
    private static String rootFolder;
    public static final String JSON_EXTENSION = ".json";

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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
        if (cls.equals(PageTransition.class) || cls.equals(LinkComponent.class)) {
            mapper.disable(MapperFeature.USE_ANNOTATIONS);
        }

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
}
