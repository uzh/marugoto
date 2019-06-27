package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.helpers.FileHelper;

@Service
public class UploadExerciseService {
	
	@Autowired
	private FileService fileService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private GameStateService gameStateService;

	public File getFileByExerciseId(String exerciseStateId) throws FileNotFoundException {
		ExerciseState exerciseState = exerciseStateService.getExerciseState(exerciseStateId);
    	File file = new File(getUploadDirectory() + File.separator + exerciseState.getInputState());
    	if (exerciseState.getInputState().equals(Constants.EMPTY_STRING) || file.exists() == false) {
    		throw new FileNotFoundException();
    	}
    	return file;
	}
	
	public Path uploadFile(MultipartFile file, String exerciseStateId) throws Exception {
    	Path savedFilePath = fileService.uploadFile(Paths.get(getUploadDirectory()), file);
    	Path filePath = fileService.renameFile(savedFilePath, exerciseStateId);
    	exerciseStateService.updateExerciseState(exerciseStateId, Paths.get(getUploadDirectory()).relativize(filePath).toString());
    	return filePath;
	}
	
	public boolean deleteFile(String exerciseStateId) throws Exception {
		File file = getFileByExerciseId(exerciseStateId);
    	boolean deleted = fileService.deleteFile(file.toPath());
    	if (deleted) {
    		exerciseStateService.updateExerciseState(exerciseStateId, Constants.EMPTY_STRING);
		}
    	return deleted;
	}
	
	public static String getUploadDirectory() {
		File folder = FileHelper.generateFolder(System.getProperty(Constants.USER_HOME_DIRECTORY), Constants.GENERATED_UPLOAD_DIRECTORY);
		return folder.getAbsolutePath();
	}
	
	/**
	 * @param gameStateId
	 * @return List<File>
	 */
	public List<File> getUploadedFilesForGameState(String gameStateId) throws FileNotFoundException {
		
		List<File> files = new ArrayList<>();
		List<ExerciseState> userExerciseStates = exerciseStateService.getUserExerciseStates(gameStateId);
		for (ExerciseState exerciseState : userExerciseStates) {
			if (exerciseState.getExercise() instanceof UploadExercise) {
				if (exerciseState.getInputState() != null) {
					File file = getFileByExerciseId(exerciseState.getId());
					files.add(file);
				}
			}
		}
		return files;
	}
	
	/**
	 * @param userId
	 * @param topicId
	 * @return List<File>
	 */
	public List<File> getUploadedFilesForTopic(String userId, String topicId) throws FileNotFoundException {
		List<GameState> gameStates = gameStateService.getByTopicAndUser(userId, topicId);

		List<File> files = new ArrayList<>();
		
		for (GameState gameState : gameStates) {
			files.addAll(getUploadedFilesForGameState(gameState.getId()));
		}
		return files;
	}
	
}
