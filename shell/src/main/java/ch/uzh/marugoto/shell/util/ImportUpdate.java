package ch.uzh.marugoto.shell.util;

import org.springframework.data.domain.Example;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.Component;

public class ImportUpdate extends BaseImport implements Importer{

    public ImportUpdate(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        saveObjectsToDatabase();
    }

    @SuppressWarnings("unchecked")
	private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var object = entry.getValue();
            var filePath = entry.getKey();

            if (isUpdateAllowed(object, filePath) == false) {
                break;
            }

            if (getRepository(object.getClass()).exists(Example.of(object)) == false) {
                System.out.println("Updating: " + filePath);
                saveObject(object, filePath);
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
            } else if (!getRepository(obj.getClass()).findById(objectId).isPresent()) {
                System.out.println("Error updating: Document is not present in DB " + filePath);
                allowed = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowed;
    }
}
