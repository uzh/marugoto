package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportOverride extends BaseImport implements Importer {

    private final HashMap<String, Object> listForRemove = new HashMap<>();

    public ImportOverride(String pathToFolder) throws Exception {
        super(pathToFolder);
    }

    @Override
    public void doImport() throws Exception {
    	objectsToDelete(getRootFolder());
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
    	
    	for (var file : FileHelper.getAllFiles(pathToDirectory)) {
            if (!file.isDirectory()) {
                removeFile(file,i);
            }
        }
    	
    	for (File directory : FileHelper.getAllSubFolders(pathToDirectory)) {
            removeFolder(directory,i);
            directory.delete();
        } 
    }

    public HashMap<String, Object> objectsToDelete(String pathToDirectory) throws Exception {

    	var files = FileHelper.getAllFiles(pathToDirectory);
    	for(File file : files) {
    		Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
    		Object objToDelete = FileHelper.generateObjectFromJsonFile(file, obj.getClass());
    		listForRemove.put(file.getAbsolutePath(),objToDelete);
    	}
    	
    	File[] directories = FileHelper.getAllSubFolders(pathToDirectory);
        for (File directory : directories) {
            if (directory.getName().contains("resources")) {
                continue;
            }
            objectsToDelete(directory.getAbsolutePath());
        }
    	return listForRemove;
    }
    
	@SuppressWarnings("unchecked")
	private void removeFile(File file, Importer i) throws Exception {
        var objects = objectsToDelete(getRootFolder());
        var objToDelete = objects.get(file.getAbsolutePath());
        
//    	Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
//    	Object objToDelete = FileHelper.generateObjectFromJsonFile(file, obj.getClass());

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
            
//            if (FileHelper.getAllFiles(file.getAbsolutePath()).length == 0) {
//               fileForRemoval.delete();
//            }
        }

        for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
            removeFolder(directory,i);
            directory.delete();
        }
    }
}
