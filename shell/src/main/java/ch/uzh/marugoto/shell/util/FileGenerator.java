package ch.uzh.marugoto.shell.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * Makes multiple folders with provided name on the provided path
     * folders will have suffix (1,2,3...)
     *
     * @param destinationPath
     * @param folderName
     * @param numberOfFolders
     * @return
     */
    public static List<File> generateFolder(String destinationPath, String folderName, int numberOfFolders) {
        List<File> generatedFolders = new ArrayList<>();

        for (int i = 1; i <= numberOfFolders; i++) {
            var name = folderName + i;
            generatedFolders.add(generateFolder(destinationPath, name));
        }

        return generatedFolders;
    }

    public static void generateJsonFileFromObject(Object object, String fileName, File destinationFolder, int numOfFiles) {
        for (int i = 1; i <= numOfFiles; i++) {
            var jsonFileName = fileName + i;
            generateJsonFileFromObject(object, jsonFileName, destinationFolder);
        }
    }

    public static void generateJsonFileFromObject(Object object, String fileName, File destinationFolder) {
        var json = new File(destinationFolder.getPath() + File.separator + fileName  + ".json");

        try {
            mapper.writeValue(json, object);
            System.out.println(fileName + " added to " + destinationFolder.getName() + " (total " + Objects.requireNonNull(destinationFolder.listFiles()).length + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
