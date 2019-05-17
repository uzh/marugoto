package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.helpers.FileHelper;

@Service
public class UploadExerciseService {
	
	@Autowired
	private FileService fileService;
	@Autowired
	private ExerciseStateService exerciseStateService;

	public File getFileByExerciseId(String exerciseStateId) throws FileNotFoundException {
        ExerciseState exerciseState = exerciseStateService.getExerciseState(exerciseStateId);
        String uploadDirectory = getUploadDirectory();
    	File file = new File(uploadDirectory + "/" + exerciseState.getInputState());
    	if (!file.exists()) {
    		throw new FileNotFoundException();
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
}
