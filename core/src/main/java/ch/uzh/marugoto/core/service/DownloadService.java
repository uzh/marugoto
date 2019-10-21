package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;

@Service
public class DownloadService {
	
	@Autowired
	private FileService fileService;
	@Autowired
    private GameStateService gameStateService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private GeneratePdfService generatePdfService;

	/**
	 * Returns compressed file with all files for one user
	 * 
	 * @param gameStateId
	 * @param userId
	 * @return zipFile 
	 * @throws FileNotFoundException
	 * @throws CreatePdfException
	 * @throws CreateZipException
	 */
	public FileInputStream getCompressedFileForUserByGameState(String gameStateId, String userId) throws FileNotFoundException, CreatePdfException, CreateZipException {
		HashMap<String, InputStream> filesInputStream = getNotebookAndUploadedFilesForUser(gameStateId, userId);
		String zipName = gameStateId.replaceAll("[^0-9]","");  
		return fileService.zipMultipleInputStreams(filesInputStream, zipName);
	}
	
	/**
	 * Returns compressed file with all files for one student
	 * 
	 * @param classId
	 * @param userId
	 * @return zipFile 
	 * @throws FileNotFoundException
	 * @throws CreatePdfException
	 * @throws CreateZipException
	 */
	public FileInputStream getCompressedFileForUserByClass(String classId, String userId) throws FileNotFoundException, CreatePdfException, CreateZipException {
		GameState gameState = gameStateService.getClassroomGameState(classId, userId);
		HashMap<String, InputStream> filesInputStream = getNotebookAndUploadedFilesForUser(gameState.getId(), userId);
		String zipName = classId.replaceAll("[^0-9]", "");
		return fileService.zipMultipleInputStreams(filesInputStream, zipName);
	}
	
	/**
	 * Returns compressed file with all files for one classroom 
	 * 
	 * @param users
	 * @param classId
	 * @return zipFile
	 * @throws FileNotFoundException
	 * @throws CreatePdfException
	 * @throws CreateZipException
	 */
	public FileInputStream getCompressedFileForClassroom(List<User> users, String classId) throws FileNotFoundException, CreatePdfException, CreateZipException {
		HashMap<String, InputStream> filesInputStream = new HashMap<>();

		for (User user : users) {
			GameState gameState = gameStateService.getClassroomGameState(classId, user.getId());
			if (gameState != null) {
				filesInputStream.putAll(getNotebookAndUploadedFilesForUser(gameState.getId(), user.getId()));
			}
		}
		String zipName = classId.replaceAll("[^0-9]", "");
		return fileService.zipMultipleInputStreams(filesInputStream, zipName);
	}
	
	/**
	 * @param gameStateId
	 * @param userId
	 * @return filesInputStream
	 * @throws FileNotFoundException
	 * @throws CreatePdfException
	 */
	public HashMap<String, InputStream> getNotebookAndUploadedFilesForUser(String gameStateId, String userId) throws FileNotFoundException, CreatePdfException {
		HashMap<String, InputStream> filesInputStream = new HashMap<>();
		User user = userRepository.findById(userId).orElseThrow();
		
		List<NotebookEntryState> notebookEntryList = notebookService.getUserNotebookEntryStates(user);
		if (notebookEntryList != null) {
			if(!notebookEntryList.isEmpty()) {
				String notebookName = Constants.NOTEBOOK_FILE_NAME_PREFIX + user.getName().toLowerCase() + Constants.PDF_EXTENSION;
				filesInputStream.put(notebookName, generatePdfService.createPdf(notebookEntryList));	
			}
		}

		List<File> uploadedFiles = uploadExerciseService.getUploadedFilesForGameState(gameStateId);
		for (File file : uploadedFiles) {
			filesInputStream.put(Constants.UPLOAD_FILE_NAME_PREFIX + DownloadService.removeIdFromFileName(file.getName()), new FileInputStream(file));
		}
		return filesInputStream;
	}
	
	/**
	 * @param fileName
	 * @return plainFileName
	 */
	static public String removeIdFromFileName(String fileName) {
		String plainFileName = fileName.substring(fileName.indexOf("-")+1);
		return plainFileName;
	}
}
