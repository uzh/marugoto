package ch.uzh.marugoto.shell.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportOverride extends BaseImport implements Importer {

	public ImportOverride(String pathToFolder) throws Exception {
		super(pathToFolder);
	}

	@Override
	public void doImport() throws Exception {
		removeFilesMarkedForDelete(getRootFolder());
		//importFiles(this);
	}

	@Override
	public void filePropertyCheck(File jsonFile, String key) throws Exception {
	}

	@Override
	public void afterImport(File jsonFile) {
		System.out.println("Overridden : " + jsonFile.getAbsolutePath());
	}

	@Override
	public void referenceFileFound(File jsonFile, String key, File referenceFile) {
		System.out.println(
				String.format("Reference found (%s): %s in file %s", key, referenceFile.getAbsolutePath(), jsonFile));
	}

	private void removeFilesMarkedForDelete(String pathToDirectory) throws Exception {

		for (var file : FileHelper.getAllFiles(pathToDirectory)) {
			removeFile(file);
		}

		for (File directory : FileHelper.getAllSubFolders(pathToDirectory)) {
			removeFolder(directory);
			if (directory.exists()) {
				removeFilesMarkedForDelete(directory.getAbsolutePath());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFile(File file) throws Exception {

		if (!file.getAbsolutePath().contains("resource")) {
			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			var repo = getRepository(obj.getClass());
			try {
				var id = FileHelper.getObjectId(file.getAbsolutePath());
				if (repo != null && id != null) {
					repo.deleteById(id);
				}
			} catch (Exception e) {
				throw new RuntimeException("Get object ID error :" + obj.getClass().getName());
			}

			file.delete();
			System.out.println("File deleted: " + file.getAbsolutePath());
		} else {
			file.delete();
		}
	}

	private void removeFolder(File file) throws Exception {

		for (var fileForRemoval : FileHelper.getAllFiles(file.getAbsolutePath())) {
			removeFile(fileForRemoval);
		}

		for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
			removeFolder(directory);
		}
		
		var dirEmpty = FileHelper.isDirEmpty(file.getAbsolutePath());
		if(dirEmpty == true) {
			FileUtils.deleteDirectory(file);
		}
	}
}
