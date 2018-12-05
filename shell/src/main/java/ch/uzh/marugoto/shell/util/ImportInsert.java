package ch.uzh.marugoto.shell.util;

import org.springframework.util.StringUtils;

import java.util.Map;

import ch.uzh.marugoto.core.data.entity.PageTransition;

public class ImportInsert extends BaseImport implements Importer {

    public ImportInsert(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        if (saveObjectsToDatabase()) {
            saveObjectsRelations();
        }
    }

    private boolean saveObjectsToDatabase() {
        var saved = true;

        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var object = entry.getValue();
            var filePath = entry.getKey();

            if (isInsertAllowed(object, filePath))  {
                saveObject(object, filePath);
            }
        }

        return saved;
    }

    @SuppressWarnings("unchecked")
    private boolean isInsertAllowed(Object obj, String filePath) {
        boolean allowed = true;

        try {
            var objectId = getObjectId(obj);

            if (obj instanceof PageTransition) {
                System.out.println("Skipping: " + filePath);
                allowed = false;
            } else if (StringUtils.isEmpty(objectId) == false) {
                allowed = false;
                System.out.println("Insert Error: " + filePath + ": File has ID present");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowed;
    }
}
