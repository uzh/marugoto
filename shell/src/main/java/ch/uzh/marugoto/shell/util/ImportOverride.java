package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.helpers.RepositoryHelper;

public class ImportOverride extends BaseImport implements Importer {

	public ImportOverride(String pathToFolder, String importerId, boolean deletePlayerStates) throws Exception {
		super(pathToFolder, importerId);
		if (deletePlayerStates) {
			removePlayerStates();
			removeTopic(hiddenFolderPath);
			FileHelper.generateImportFolder(pathToFolder, importerId);
		} else {
			File topicJson = Paths.get(hiddenFolderPath.concat("/topic.json")).toFile();
			Topic topicObj = (Topic) FileHelper.generateObjectFromJsonFile(topicJson, Topic.class);
			topicObj.setActive(false);
			saveObject(topicObj);
			// rename existing hidden folder
			new File(hiddenFolderPath).renameTo(new File(FileHelper.getPathToImporterFolder(pathToFolder, importerId.concat(" - inactive"))));
		}
	}

	@Override
	public void doImport() throws Exception {
		super.doImport(this, hiddenFolderPath);
	}

	@Override
	public void beforeImport(File jsonFile) {}

	@Override
	public void afterImport(File jsonFile) {
		System.out.println("Overridden : " + jsonFile.getAbsolutePath());
	}

	private void removeTopic(String pathToDirectory) throws Exception {

		for (var file : FileHelper.getAllFiles(pathToDirectory)) {
			removeFile(file);
		}

		for (File directory : FileHelper.getAllSubFolders(pathToDirectory)) {
			removeFolder(directory);
			if (directory.exists()) {
				removeTopic(directory.getAbsolutePath());
			}
		}

		if (FileHelper.isDirEmpty(pathToDirectory)) {
			FileUtils.deleteDirectory(new File(pathToDirectory));
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFile(File file) throws Exception {

		if (file.getAbsolutePath().contains("resource") == false) {
			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			var repo = RepositoryHelper.getRepository(obj.getClass());
			try {
				var id = FileHelper.getObjectId(file.getAbsolutePath());
				if (repo != null && !id.isEmpty()) {
					repo.deleteById(id);
				}
			} catch (Exception e) {
				throw new RuntimeException("Get object ID error :" + obj.getClass().getName());
			}
		}

		file.delete();
		System.out.println("File deleted: " + file.getAbsolutePath());
	}

	private void removeFolder(File file) throws Exception {

		for (var fileForRemoval : FileHelper.getAllFiles(file.getAbsolutePath())) {
			removeFile(fileForRemoval);
		}

		for (File directory : FileHelper.getAllSubFolders(file.getAbsolutePath())) {
			removeFolder(directory);
		}

		var dirEmpty = FileHelper.isDirEmpty(file.getAbsolutePath());
		if (dirEmpty) {
			FileUtils.deleteDirectory(file);
		}
	}

	/**
	 * Find topic ID
	 *
	 * @return
	 */
	private String getTopicId() {
		File topicJson = Paths.get(hiddenFolderPath.concat("/topic.json")).toFile();
		return FileHelper.getObjectId(topicJson.getAbsolutePath());
	}

	private void removePlayerStates() throws Exception {
		var gameStates = RepositoryHelper.getGameStatesForTopic(getTopicId());
		for (GameState gameState : gameStates) {
			RepositoryHelper.deleteUserStates(gameState.getId());
			RepositoryHelper.deleteDialogStates(gameState.getId());
			RepositoryHelper.deleteMailStates(gameState.getId());
			RepositoryHelper.deleteNotebookEntryStates(gameState.getId());

			var pageStates = RepositoryHelper.findPageStatesByGameState(gameState.getId());

			if(pageStates != null) {
				for (PageState pageState : pageStates) {
					RepositoryHelper.deleteExerciseStates(pageState.getId());
				}
			}

			RepositoryHelper.deletePageStates(gameState.getId());
			RepositoryHelper.delete(gameState);
		}
	}
}
