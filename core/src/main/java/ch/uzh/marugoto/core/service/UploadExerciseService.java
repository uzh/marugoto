package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
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
	
	public void uploadFile(MultipartFile file, String exerciseStateId) throws ParseException, Exception {
    	String savedFile = fileService.uploadFile(file);
    	String newFileName = fileService.renameFile(Paths.get(savedFile), exerciseStateId);
    	exerciseStateService.updateExerciseState(exerciseStateId, newFileName);
	}
	
	public void deleteFile(String exerciseStateId) throws ParseException, Exception {
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
		
		File folder = FileHelper.generateFolder(System.getProperty(Constants.FILE_UPLOADS_DIRECTORY), Constants.GENERATED_UPLOAD_DIRECTORY);
		return folder.getAbsolutePath();
	}
}
