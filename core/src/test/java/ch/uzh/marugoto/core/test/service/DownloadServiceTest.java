package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.Gender;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.service.DownloadService;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.UploadExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class DownloadServiceTest extends BaseCoreTest {

	@Autowired
	private DownloadService downloadService;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private GameStateRepository gameStateRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
	private ClassroomRepository classroomRepository;
	@Autowired
	private ClassroomService classroomService;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired 
	private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private NotebookEntryStateRepository notebookEntryStateRepository;
    
    private User user;
    private ExerciseState exerciseState;
	private InputStream inputStream;
	private MockMultipartFile file;
	private String exerciseStateId;
	private GameState gameState;
	private NotebookEntry notebookEntry;
	private PageState pageState;

	@Before
	public void init() throws IOException {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
		pageState = user.getCurrentPageState();
		gameState = gameStateRepository.findByUserId(user.getId()).get(0);
		
		notebookEntry = notebookService.getNotebookEntry(pageState.getPage()).orElse(null);
        notebookEntryStateRepository.save(new NotebookEntryState(gameState, notebookEntry));

		exerciseState = exerciseStateRepository.findByPageStateId(user.getCurrentPageState().getId()).get(0);
		inputStream = new URL("https://picsum.photos/600").openStream();
		file = new MockMultipartFile("file", "image.jpg", "image/jpeg", inputStream);
		exerciseStateId = exerciseState.getId().replaceAll("[^0-9]", "");
	}

	
	@Test
	public void testGetNotebookAndUploadedFilesForUser() throws Exception {
		Page page = pageRepository.findByTitle("Page 2");
		GameState gameState = gameStateRepository.findByUserId(user.getId()).get(0);
		UploadExercise uploadExercise = new UploadExercise();
		uploadExercise.setPage(page);
		componentRepository.save(uploadExercise);

		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		var exerciseStates = exerciseStateService.getUserExerciseStates(gameState.getId());
		for (ExerciseState exerciseState : exerciseStates) {
			if (exerciseState.getExercise() instanceof UploadExercise) {
				exerciseStateId = exerciseState.getId();
			}
		}
		uploadExerciseService.uploadFile(file, exerciseStateId);
		notebookService.initializeStateForNewPage(user);

		//downloadService.getNotebookAndUploadedFilesForUser(gameState.getId(), user.getId());
		//HashMap<String, InputStream> filesInputStream = 
		//assertNotNull(filesInputStream);
		//assertEquals(filesInputStream.size(), 2);
	}

	
	@Test
	public void testGetNotebookAndUploadedFilesForClassrom()
			throws FileNotFoundException, CreatePdfException, CreateZipException {
		var testUser = new User(Gender.Male, "Marugoto", "Test", "notebooktest@marugoto.ch",
				new BCryptPasswordEncoder().encode("test"));
		userRepository.save(testUser);
		Page page2 = pageRepository.findByTitle("Page 2");
		Topic topic = topicRepository.findAll().iterator().next();
		var testGameState = gameStateRepository.save(new GameState(topic, testUser));
		testUser.setCurrentGameState(testGameState);
		userRepository.save(testUser);

		String invitationLink = classroomService.createInvitationLinkForClassroom();
		Classroom classroom = new Classroom();
		classroom.setInvitationLinkId(invitationLink);
		classroomRepository.save(classroom);
		classroomService.addUserToClassroom(user, invitationLink);
		classroomService.addUserToClassroom(testUser, invitationLink);
		testGameState.setClassroom(classroom);
		gameStateRepository.save(testGameState);

		pageStateService.initializeStateForNewPage(page2, testUser);
		notebookService.initializeStateForNewPage(testUser);

		var users = classroomService.getClassroomMembers(classroom.getId());
		FileInputStream filesInputStream = downloadService.getCompressedFileForClassroom(users, classroom.getId());
		assertNotNull(filesInputStream);
	}
}
