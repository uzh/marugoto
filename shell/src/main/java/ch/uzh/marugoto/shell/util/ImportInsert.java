package ch.uzh.marugoto.shell.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.shell.helpers.JsonFileCheckerHelper;

public class ImportInsert extends BaseImport implements Importer {

    public ImportInsert(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        truncateDatabase();

        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var jsonFile = new File(entry.getKey());

            try {
                checkForRelationReferences(jsonFile);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
