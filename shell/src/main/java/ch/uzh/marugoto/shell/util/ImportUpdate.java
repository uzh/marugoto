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

            if (!isUpdateAllowed(object, filePath)) {
                break;
            }

            if (!getRepository(object.getClass()).exists(Example.of(object))) {
                System.out.println("Updating: " + filePath);
                saveObject(object, filePath);
            }
        }
    }

    @SuppressWarnings("unchecked")
	private boolean isUpdateAllowed(Object obj, String filePath) {
        boolean allowed = true;
        try {
            Field id;
            if (obj instanceof Component) {
                id = getEntityClassByName("Component").getDeclaredField("id");
            } else {
                id = obj.getClass().getDeclaredField("id");
            }

            id.setAccessible(true);
            var idValue = id.get(obj);

            if (idValue == null || StringUtils.isEmpty(idValue)) {
                System.out.println("Error updating: ID is missing in file " + filePath);
                allowed = false;
            } else if (!getRepository(obj.getClass()).findById(idValue).isPresent()) {
                System.out.println("Error updating: Document is not present in DB " + filePath);
                allowed = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowed;
    }
}
