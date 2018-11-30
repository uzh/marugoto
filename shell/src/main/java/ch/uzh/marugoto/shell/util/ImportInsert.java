package ch.uzh.marugoto.shell.util;

import org.springframework.util.StringUtils;

import java.util.Map;

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

            if (!isInsertAllowed(object, filePath)) {
                saved = false;
                break;
            }

            saveObject(object, filePath);
        }

        return saved;
    }

    @SuppressWarnings("unchecked")
    private boolean isInsertAllowed(Object obj, String filePath) {
        boolean allowed = true;

        try {
            var objectId = getObjectId(obj);
            if (!StringUtils.isEmpty(objectId)) {
                allowed = false;
                System.out.println("Insert Error: " + filePath + ": File has ID present");
                System.out.println("Stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowed;
    }
}
