package ch.uzh.marugoto.shell.util;

import java.io.File;


import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportOverride extends BaseImport implements Importer {

    public ImportOverride(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() throws Exception {
    	removeFilesMarkedForDelete(getRootFolder(),this);
        importFiles(this);
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

    private void removeFilesMarkedForDelete(String pathToDirectory, Importer i) throws Exception {
        for (File directory : FileHelper.getAllSubFolders(pathToDirectory)) {
            removeFolder(directory,i);
            directory.delete();
        }
        for (var file : FileHelper.getAllFiles(pathToDirectory)) {
            if (!file.isDirectory()) {
                removeFile(file,i);
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void removeFile(File file, Importer i) throws Exception {
    	Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
    	//checkFilePropetiesAndReferences(file,i);
    	Object objToDelete = FileHelper.generateObjectFromJsonFile(file, obj.getClass());
    	
    	
        if (objToDelete != null) {
            var repo = getRepository(objToDelete.getClass());
            try {
                var id = getObjectId(objToDelete);
                if (repo != null && id != null) {
                    repo.delete(objToDelete);
                }
            } catch (Exception e) {
                throw new RuntimeException("Get object ID error :" + objToDelete.getClass().getName());
            }
        }

        file.delete();
        System.out.println("File deleted: " + file.getAbsolutePath());
    }

    private void removeFolder(File file,Importer i) throws Exception {
        for (var fileForRemoval : FileHelper.getAllFiles(file.getAbsolutePath())) {
            removeFile(fileForRemoval,i);
        }

        for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
            removeFolder(directory,i);
            directory.delete();
        }
    }
}
