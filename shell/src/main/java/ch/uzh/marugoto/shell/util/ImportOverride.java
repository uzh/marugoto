package ch.uzh.marugoto.shell.util;

import java.io.File;

import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportOverride extends BaseImport implements Importer {

    public ImportOverride(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        importFiles(this);
        removeFilesMarkedForDelete(getRootFolder());
    }

    @Override
    public void filePropertyCheck(File jsonFile, String key) throws Exception {}

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Overridden : " + jsonFile.getAbsolutePath());
    }

    @Override
    public void referenceFileFound(File jsonFile, String key, File referenceFile) {
        System.out.println(String.format("Reference found (%s): %s in file %s", key, referenceFile.getAbsolutePath(), jsonFile));
    }

    private void removeFilesMarkedForDelete(String pathToDirectory) {
        for (var file : FileHelper.getAllFiles(pathToDirectory)) {
            if (file.getName().toLowerCase().contains("delete")) {
                removeFile(file);
            }
        }

        for (File directory : FileHelper.getAllSubFolders(pathToDirectory)) {
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
                throw new RuntimeException("Get object ID error :" + objToDelete.getClass().getName());
            }
        }

        file.delete();
        System.out.println("File deleted: " + file.getAbsolutePath());
    }

    private void removeFolder(File file) {
        for (var fileForRemoval : FileHelper.getAllFiles(file.getAbsolutePath())) {
            removeFile(fileForRemoval);
        }

        for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
            removeFolder(directory);
            directory.delete();
        }
    }
}
