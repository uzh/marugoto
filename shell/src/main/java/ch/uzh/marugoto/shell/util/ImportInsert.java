package ch.uzh.marugoto.shell.util;

import java.util.Map;

public class ImportInsert extends BaseImport implements Importer {

    public ImportInsert(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        truncateDatabase();
        saveObjectsToDatabase();
        saveObjectsRelations();
    }

    private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            saveObject(entry.getValue(), entry.getKey());
        }
    }
}
