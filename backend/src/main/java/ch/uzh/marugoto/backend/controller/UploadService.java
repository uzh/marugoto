package ch.uzh.marugoto.backend.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.FileService;

@Service
public class UploadService {
	
	@Autowired
	private FileService fileService;
	@Autowired
	private ExerciseStateService exerciseStateService;

	public File getFileById(String exerciseStateId) throws FileNotFoundException {
        ExerciseState exerciseState = exerciseStateService.getExerciseState(exerciseStateId);
        String uploadDirectory = fileService.getUploadDirectory();
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
	
	public void deleteFile(String exerciseStateId) throws IOException {
        ExerciseState exerciseState = exerciseStateService.getExerciseState(exerciseStateId);
        String uploadDirectory = fileService.getUploadDirectory();
    	File file = new File(uploadDirectory + "/" + exerciseState.getInputState());
    	if (!file.exists()) {
    		throw new FileNotFoundException();
    	}
    	fileService.deleteFile(Paths.get(file.getAbsolutePath()));
	}
}
