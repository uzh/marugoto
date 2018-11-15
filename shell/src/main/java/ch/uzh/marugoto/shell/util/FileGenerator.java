package ch.uzh.marugoto.shell.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileGenerator {

    private final static ObjectMapper mapper = new ObjectMapper()
            .disable(MapperFeature.USE_ANNOTATIONS)
            .disable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING);

    public static File generateFolder(String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        System.out.println(folder.getPath());
        return folder;
    }

    public static void generateJsonFilesFromObject(Object object, int numOfFiles, String fileName, File destinationFolder) {

        for (int i = 1; i <= numOfFiles; i++) {
            var jsonFileName = fileName + i + ".json";
            var json = new File(destinationFolder.getPath() + File.separator + jsonFileName);

            try {
                mapper.writeValue(json, object);
                System.out.println(jsonFileName + " created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(StringUtils.capitalize(destinationFolder.getName()) + " folder has " + Objects.requireNonNull(destinationFolder.listFiles()).length + " files");
    }
}
