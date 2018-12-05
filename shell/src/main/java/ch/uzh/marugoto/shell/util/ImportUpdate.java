package ch.uzh.marugoto.shell.util;

import org.springframework.data.domain.Example;
import org.springframework.util.StringUtils;

import java.util.Map;

import ch.uzh.marugoto.core.data.entity.PageTransition;

public class ImportUpdate extends BaseImport implements Importer {

    public ImportUpdate(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        try {
            saveObjectsToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var object = entry.getValue();
            var filePath = entry.getKey();

            if (isUpdateAllowed(object, filePath)) {
                if (getRepository(object.getClass()).exists(Example.of(object)) == false) {
                    saveObject(object, filePath);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	private boolean isUpdateAllowed(Object obj, String filePath) {
        boolean allowed = true;

        try {
            var objectId = getObjectId(obj);

            if (StringUtils.isEmpty(objectId)) {
                System.out.println("Error updating: ID is missing in file " + filePath);
                allowed = false;
            } else if (getRepository(obj.getClass()).existsById(objectId) == false) {
                System.out.println("Error updating: Document is not present in DB " + filePath);
                allowed = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowed;
    }
}
