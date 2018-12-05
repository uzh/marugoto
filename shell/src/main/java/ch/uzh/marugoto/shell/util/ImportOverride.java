package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.util.Map;

public class ImportOverride extends BaseImport implements Importer {

    public ImportOverride(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        saveObjectsToDatabase();
        saveObjectsRelations();
        removeFilesMarkedForDelete(getRootFolder().getAbsolutePath());
    }

    private void removeFilesMarkedForDelete(String pathToDirectory) {
        for (var file : FileService.getAllFiles(pathToDirectory)) {
            if (file.getName().toLowerCase().contains("delete")) {
                removeFile(file);
            }
        }

        for (File directory : FileService.getAllDirectories(pathToDirectory)) {
            if (directory.getName().toLowerCase().contains("delete")) {
                removeFolder(directory);
                directory.delete();
            } else {
                removeFilesMarkedForDelete(directory.getAbsolutePath());
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void removeFile(File file) {
        var objects = getObjectsForImport();
        var objToDelete = objects.get(file.getAbsolutePath());

        if (objToDelete != null) {
            var repo = getRepository(objToDelete.getClass());
            try {
                var id = getObjectId(objToDelete);
                if (repo != null && id != null) {
                    repo.delete(objToDelete);
                    objects.remove(file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("Get object ID error :" + objToDelete.getClass().getName());
                e.printStackTrace();
            }
        }

        file.delete();
    }

    private void removeFolder(File file) {
        for (var fileForRemoval : FileService.getAllFiles(file.getAbsolutePath())) {
            removeFile(fileForRemoval);
        }

        for (File directory : FileService.getAllDirectories(file.getAbsolutePath())) {
            removeFolder(directory);
            directory.delete();
        }
    }

    private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var filePath = entry.getKey();
            var obj = entry.getValue();

            saveObject(obj, filePath);
        }
    }
}
