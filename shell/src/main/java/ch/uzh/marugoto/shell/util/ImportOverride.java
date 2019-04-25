package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ch.uzh.marugoto.core.data.entity.state.DialogState;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.DialogStateRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportOverride extends BaseImport implements Importer {

	public ImportOverride(String pathToFolder,String importerId) throws Exception {
		super(pathToFolder,importerId);
	}

	@Override
	public void doImport() throws Exception {
		removePlayerStates(getRootFolder());
		removeFilesMarkedForDelete(getRootFolder());
		prepareObjectsForImport(getFolderPath(getInitalPath(),FileHelper.getImporterIdFolderName()));
		importFiles(this);
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

		var dirEmpty = FileHelper.isDirEmpty(pathToDirectory);
		if (dirEmpty == true) {
			FileUtils.deleteDirectory(new File(pathToDirectory));
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFile(File file) throws Exception {

		if (!file.getAbsolutePath().contains("resource")) {
			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			var repo = getRepository(obj.getClass());
			try {
				var id = FileHelper.getObjectId(file.getAbsolutePath());
				if (repo != null && !id.isEmpty()) {
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
		if (dirEmpty == true) {
			FileUtils.deleteDirectory(file);
		}
	}

	private String getTopicId(String pathToDirectory) throws Exception {
		String id = null;
		for (var file : FileHelper.getAllFiles(pathToDirectory)) {
			Object obj = getEntityClassByName(file.getName()).getDeclaredConstructor().newInstance();
			if (obj instanceof Topic) {
				id = FileHelper.getObjectId(file.getAbsolutePath());
			}
		}
		return id;
	}

	private List<PageState> findPageStatesByGameState(String gameStateId) {
		PageStateRepository pageStateRepository = BeanUtil.getBean(PageStateRepository.class);
		return pageStateRepository.findUserPageStates(gameStateId);
	}

	private void deleteExerciseStates(String pageStateId) {
		ExerciseStateRepository exerciseStateRepository = BeanUtil.getBean(ExerciseStateRepository.class);
		List<ExerciseState> exerciseStates = exerciseStateRepository.findByPageStateId(pageStateId);
		for (ExerciseState exerciseState : exerciseStates) {
			exerciseStateRepository.delete(exerciseState);
		}
	}

	private void deleteDialogStates(String gameStateId) {
		DialogStateRepository dialogStateRepository = BeanUtil.getBean(DialogStateRepository.class);
		List<DialogState> dialogStates = dialogStateRepository.findByGameState(gameStateId);
		for (DialogState dialogState : dialogStates) {
			dialogStateRepository.delete(dialogState);
		}
	}

	private void deleteMailStates(String gameStateId) {
		MailStateRepository mailStateRepository = BeanUtil.getBean(MailStateRepository.class);
		List<MailState> mailStates = mailStateRepository.findAllForGameState(gameStateId);
		for (MailState mailState : mailStates) {
			mailStateRepository.delete(mailState);
		}
	}

	private void deleteNotebookEntryStates(String gameStateId) {
		NotebookEntryStateRepository notebookEntryStateRepository = BeanUtil
				.getBean(NotebookEntryStateRepository.class);
		List<NotebookEntryState> notebookEntryStates = notebookEntryStateRepository
				.findUserNotebookEntryStates(gameStateId);
		for (NotebookEntryState notebookEntryState : notebookEntryStates) {
			notebookEntryStateRepository.delete(notebookEntryState);
		}
	}

	private void deletePageStates(String gameStateId) {
		PageStateRepository pageStateRepository = BeanUtil.getBean(PageStateRepository.class);
		var pageStates = pageStateRepository.findUserPageStates(gameStateId);
		for (PageState pageState : pageStates) {
			pageStateRepository.delete(pageState);
		}
	}

	private void removePlayerStates(String pathToDirectory) throws Exception {
		GameStateRepository gameStateRepository = BeanUtil.getBean(GameStateRepository.class);
		List<GameState> gameStates = gameStateRepository.findByTopicId(getTopicId(pathToDirectory));
		for (GameState gameState : gameStates) {
			deleteDialogStates(gameState.getId());
			deleteMailStates(gameState.getId());
			deleteNotebookEntryStates(gameState.getId());
			var pageStates = findPageStatesByGameState(gameState.getId());
			for (PageState pageState : pageStates) {
				deleteExerciseStates(pageState.getId());
			}
			deletePageStates(gameState.getId());
			gameStateRepository.delete(gameState);
		}
	}
}
