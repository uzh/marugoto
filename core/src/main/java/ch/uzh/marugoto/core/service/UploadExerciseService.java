package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        String uploadDirectory = getUploadDirectory();
    	File file = new File(uploadDirectory + "/" + exerciseState.getInputState());
    	if (!file.exists()) {
    		file = new File(Constants.EMPTY_STRING);
    	}
    	return file;
	}
	
	public void uploadFile(MultipartFile file, String exerciseStateId) throws Exception {
    	Path savedFilePath = fileService.uploadFile(Paths.get(getUploadDirectory()), file);
    	Path filePath = fileService.renameFile(savedFilePath, exerciseStateId);
    	exerciseStateService.updateExerciseState(exerciseStateId, filePath.getFileName().toString());
	}
	
	public void deleteFile(String exerciseStateId) throws Exception {
        ExerciseState exerciseState = exerciseStateService.getExerciseState(exerciseStateId);
        String uploadDirectory = getUploadDirectory();
    	File file = new File(uploadDirectory + "/" + exerciseState.getInputState());
    	if (!file.exists()) {
    		throw new FileNotFoundException();
    	}
    	fileService.deleteFile(Paths.get(file.getAbsolutePath()));
    	exerciseStateService.updateExerciseState(exerciseStateId, Constants.EMPTY_STRING);
	}
	
	public static String getUploadDirectory() {
		File folder = FileHelper.generateFolder(System.getProperty(Constants.USER_HOME_DIRECTORY), Constants.GENERATED_UPLOAD_DIRECTORY);
		return folder.getAbsolutePath();
	}
	
	/**
	 * @param userId
	 * @param topicId
	 * @return List<File>
	 * @throws FileNotFoundException 
	 */
	public List<File> getUploadedFiles(String userId, String topicId) throws FileNotFoundException {
		List<GameState> gameStates = gameStateService.getByTopicAndUser(userId, topicId);

		List<File> files = new ArrayList<>();
		
		for (GameState gameState : gameStates) {
			List<ExerciseState> userExerciseStates = exerciseStateService.getUserExerciseStates(gameState.getId());
			for (ExerciseState exerciseState : userExerciseStates) {
				if (exerciseState.getExercise() instanceof UploadExercise) {
					File file = getFileByExerciseId(exerciseState.getId());
					if(file.exists()) {
						files.add(file);	
					}
				}
			}
		}
		return files;
	}
	
	
}
