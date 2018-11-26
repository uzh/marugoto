package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileGenerator {

    private final static ObjectMapper mapper = new ObjectMapper()
            .disable(MapperFeature.USE_ANNOTATIONS)
            .disable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING);

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
    public static void generateJsonFileFromObject(Object object, String fileName, File destinationFolder, int numOfFiles) {
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
            mapper.writeValue(json, object);
            System.out.println(fileName + " added to " + destinationFolder.getName() + " (total " + Objects.requireNonNull(destinationFolder.listFiles()).length + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param pathToDirectory
     * @return
     */
    public static File[] getAllFiles(String pathToDirectory) {
        File folder = new File(pathToDirectory);
        return folder.listFiles(file -> !file.isHidden());
    }

    public static File[] getAllDirectories(String pathToDirectory) {
        File folder = new File(pathToDirectory);
        return folder.listFiles(file -> !file.isHidden() && file.isDirectory());
    }
}
