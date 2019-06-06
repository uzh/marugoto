package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.NotebookContentCreateAt;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookContentRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;

@Service
public class NotebookService {

	@Autowired
	private NotebookEntryRepository notebookEntryRepository;
	@Autowired
	private NotebookEntryStateRepository notebookEntryStateRepository;
	@Autowired
	private NotebookContentRepository notebookContentRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private GameMailService gameMailService;
	@Autowired
	private GeneratePdfService generatePdfService;
	@Autowired
	private FileService fileService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
    private GameStateService gameStateService;

	/**
	 * Returns all notebook entry states for user
	 *
	 * @param user
	 * @return notebookEntries list
	 */
	public List<NotebookEntryState> getUserNotebookEntryStates(User user) {
		return notebookEntryStateRepository.findUserNotebookEntryStates(user.getCurrentGameState().getId());
	}

	/**
	 * Finds notebook entry by page
	 *
	 * @param page
	 * @return notebookEntry
	 */
	public Optional<NotebookEntry> getNotebookEntry(Page page) {
		return notebookEntryRepository.findNotebookEntryByPage(page.getId());
	}

	public void initializeStateForNewPage(User user) {
		Page currentPage = user.getCurrentPageState().getPage();
		NotebookEntry notebookEntry = getNotebookEntry(currentPage).orElse(null);

		if (notebookEntry != null) {
			notebookEntryStateRepository.save(new NotebookEntryState(user.getCurrentGameState(), notebookEntry));
		}

		addNotebookContentForPage(user, NotebookContentCreateAt.pageEnter);
	}

	/**
	 * Creates NotebookContent for the last NotebookEntryState
	 * 
	 * @param user
	 * @param notebookContentCreateAt pageEnter/pageExit
	 */
	public void addNotebookContentForPage(User user, NotebookContentCreateAt notebookContentCreateAt) {
		NotebookEntryState notebookEntryState = notebookEntryStateRepository.findLastNotebookEntryState(user.getCurrentGameState().getId());

		createComponentNotebookContent(user, notebookEntryState, notebookContentCreateAt);
		// only on page exit, mail notebook content is created
		if (notebookContentCreateAt.equals(NotebookContentCreateAt.pageExit)) {
			createMailNotebookContent(user, notebookEntryState);
		}
	}

	/**
	 * Creates page components content in notebook
	 *
	 * @param user
	 * @param notebookEntryState
	 * @param notebookContentCreateAt
	 */
	private void createComponentNotebookContent(User user, NotebookEntryState notebookEntryState, NotebookContentCreateAt notebookContentCreateAt) {
		Page currentPage = user.getCurrentPageState().getPage();
		for (Component component : componentRepository.findPageComponents(currentPage.getId())) {
			if (component.isShownInNotebook() && component.getShowInNotebookAt() == notebookContentCreateAt) {
				NotebookContent notebookContent = new NotebookContent(component);
				if (component instanceof Exercise) {
					createExerciseNotebookContent(user, (Exercise)component, notebookContent);
				}
				createNotebookContent(notebookEntryState, notebookContent);
			}
		}
	}

	/**
	 * Add notebook content for exercise
	 *
	 * @param user
	 * @param exercise
	 */
	public void createExerciseNotebookContent(User user, Exercise exercise, NotebookContent notebookContent) {
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(user.getCurrentPageState().getId(), exercise.getId()).orElseThrow();
		notebookContent.setExerciseState(exerciseState);
		notebookContent.setDescription(exercise.getDescriptionForNotebook());
	}

	/**
	 * Create mail notebook content
	 *
	 * @param user
	 */
	public void createMailNotebookContent(User user, NotebookEntryState notebookEntryState) {
		Page currentPage = user.getCurrentPageState().getPage();

		for (MailState mailState : gameMailService.getReceivedMailsForPage(user, currentPage)) {
			if (mailState != null && mailState.getMail().isShownInNotebook()) {
				createNotebookContent(notebookEntryState, new NotebookContent(mailState));
			}
		}
	}

	/**
	 * Save notebookContent to database and add it to NotebookEntryState
	 *
	 * @param notebookEntryState
	 * @param notebookContent
	 */
	private void createNotebookContent(NotebookEntryState notebookEntryState, NotebookContent notebookContent) {
		notebookEntryState.addNotebookContent(notebookContentRepository.save(notebookContent));
		notebookEntryStateRepository.save(notebookEntryState);
	}

	/**
	 * Creates user personal note
	 *
	 * @param notebookEntryStateId
	 * @param markdownContent
	 * @return personalNote
	 */
	public PersonalNote createPersonalNote(String notebookEntryStateId, String markdownContent) {
		NotebookEntryState notebookEntryState = notebookEntryStateRepository
				.findNotebookEntryStateById(notebookEntryStateId).orElseThrow();

		PersonalNote personalNote = new PersonalNote(markdownContent);
		createNotebookContent(notebookEntryState, new NotebookContent(personalNote));

		return personalNote;
	}

	/**
	 * Updates personal note
	 *
	 * @param notebookContentId
	 * @param markdownContent
	 * @return personalNote
	 */
	public PersonalNote updatePersonalNote(String notebookContentId, String markdownContent) {
		NotebookContent notebookContent = notebookContentRepository.findById(notebookContentId).orElseThrow();

		PersonalNote personalNote = notebookContent.getPersonalNote();
		personalNote.setMarkdownContent(markdownContent);

		notebookContent.setPersonalNote(personalNote);
		notebookContentRepository.save(notebookContent);

		return personalNote;
	}

	/**
	 * @param gameStateId
	 * @param userId
	 * @return filesInputStream
	 * @throws FileNotFoundException
	 * @throws CreatePdfException
	 */
	public HashMap<String, InputStream> getNotebookAndUploadedFilesForUser(String gameStateId, String userId) throws FileNotFoundException, CreatePdfException {

		User user = userRepository.findById(userId).orElseThrow();
		List<File> uploadedFiles = uploadExerciseService.getUploadedFilesForGameState(gameStateId);
		
		HashMap<String, InputStream> filesInputStream = new HashMap<>();
		List<NotebookEntryState> notebookEntryList = getUserNotebookEntryStates(user);
		if (notebookEntryList != null) {
			if(!notebookEntryList.isEmpty()) {
				String notebookName = Constants.NOTEBOOK_FILE_NAME_PREFIX + user.getName().toLowerCase();
				filesInputStream.put(notebookName, generatePdfService.createPdf(notebookEntryList));	
			}
		}
		for (File file : uploadedFiles) {
			filesInputStream.put(Constants.UPLOAD_FILE_NAME_PREFIX + file.getName(), new FileInputStream(file));
		}
		return filesInputStream;
	}
	
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
		return fileService.zipMultipleInputStreams(filesInputStream, classId);
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
		String zipName = classId.replaceAll("[^0-9]","");
		return fileService.zipMultipleInputStreams(filesInputStream, zipName);
	}
}
