package ch.uzh.marugoto.shell.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.shell.deserializer.CriteriaDeserializer;
import ch.uzh.marugoto.shell.deserializer.ImageComponentDeserializer;

public class FileService {

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(MapperFeature.USE_ANNOTATIONS)
            .disable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING);

    public static ObjectMapper getMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Criteria.class, new CriteriaDeserializer());
        module.addDeserializer(ImageComponent.class, new ImageComponentDeserializer());
        mapper.registerModule(module);

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
    public static void generateInitialJsonFilesFromObject(Object object, String fileName, File destinationFolder, int numOfFiles) {
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
        var json = new File(destinationFolder.getPath() + File.separator + fileName  + ".json");

        try {
            getMapper().writeValue(json, object);
            System.out.println(fileName + " added to " + destinationFolder.getName() + " (total " + Objects.requireNonNull(destinationFolder.listFiles()).length + ")");
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

    public static Object generateObjectFromJsonFile(File file, Class<?>cls) throws IOException {
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
}
