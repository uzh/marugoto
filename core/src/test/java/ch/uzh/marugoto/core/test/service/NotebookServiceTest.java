package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

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
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.NotebookContentCreateAt;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.UploadExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookServiceTest extends BaseCoreTest {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private NotificationRepository notificationRepository;;
    @Autowired
    private NotebookEntryStateRepository notebookEntryStateRepository;
    @Autowired
    private MailStateRepository mailStateRepository;
    @Autowired
    private GameStateRepository gameStateRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired 
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
	private ClassroomRepository classroomRepository;
	@Autowired
	private ClassroomService classroomService;
	@Autowired
	private TopicRepository topicRepository;
	
	
    private Mail mail;
    private User user;
    private PageState pageState;
    private GameState gameState;
    private NotebookEntry notebookEntry;
    private NotebookEntryState notebookEntryState;
	private ExerciseState exerciseState;
	private InputStream inputStream;
	private MockMultipartFile file;
	private String exerciseStateId;
	
    @Before
	public void init() throws IOException {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        
        exerciseState = exerciseStateRepository.findByPageStateId(user.getCurrentPageState().getId()).get(0);
		inputStream = new URL("https://picsum.photos/600").openStream();
		file = new MockMultipartFile("file", "image.jpg", "image/jpeg", inputStream);
		exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
        var dr = new DialogResponse();
        pageState = user.getCurrentPageState();
        dr.setButtonText("Yes");
        Page page6 = pageRepository.findByTitle("Page 6");
        mail = notificationRepository.findMailNotificationsForPage(page6.getId()).get(0);
        gameState = gameStateRepository.findByUserId(user.getId()).get(0);
        notebookEntry = notebookService.getNotebookEntry(pageState.getPage()).orElse(null);
        notebookEntryState = notebookEntryStateRepository.save(new NotebookEntryState(gameState, notebookEntry));
    }

    @Test
    public void testGetUserNotebookEntryStates() {
    	var page2 = pageRepository.findByTitle("Page 2");
    	var notebookEntry = notebookEntryRepository.save(new NotebookEntry(page2, "title"));
        notebookEntryStateRepository.save(new NotebookEntryState(user.getCurrentGameState(), notebookEntry));
    	
        var testEntries = notebookService.getUserNotebookEntryStates(user);
        assertEquals(2, testEntries.size());
    }

    @Test(expected = Exception.class)
    public void testGetNotebookEntry() {
        var pageState = user.getCurrentPageState();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage()).orElse(null);

        assertNotNull(notebookEntry);
        assertEquals(pageState.getPage().getTitle(), notebookEntry.getPage().getTitle());

        //test getNotebookEntryForMail
        notebookEntry = new NotebookEntry(mail, "title");
        notebookEntryRepository.save(notebookEntry);
        var notebookEntryForMailExercise = notebookService.getNotebookEntryForMail(mail);
        assertNotNull(notebookEntryForMailExercise);
        
        // expected exception
        var page4 = pageRepository.findByTitle("Page 4");
        notebookService.getNotebookEntry(page4).orElseThrow();
    }
    
    @Test
    public void testAddNotebookContentForPage() throws PageTransitionNotAllowedException {

    	var textComponent = new TextComponent(6, "Some example text for component", pageState.getPage());
		textComponent.setShowInNotebook(true);
		textComponent.setShowInNotebookAt(NotebookContentCreateAt.pageEnter);
		notebookService.addNotebookContentForPage(user, NotebookContentCreateAt.pageEnter);
		
		NotebookEntryState newNotebookEntryState = notebookEntryStateRepository.findNotebookEntryStateById(notebookEntryState.getId()).orElseThrow();
		var component = newNotebookEntryState.getNotebookContent().get(0).getComponent();
		TextComponent tc = (TextComponent) component; 
		assertEquals(tc.getMarkdownContent(), textComponent.getMarkdownContent());
    }
    
    @Test
    public void testCreateMailNotebookContent() {

        notebookEntry.setMail(mail);
        notebookEntryRepository.save(notebookEntry);
        MailState mailState = mailStateRepository.save(new MailState (notebookEntryState.getNotebookEntry().getMail(),user.getCurrentGameState())); 		
    	notebookService.createMailNotebookContent(mailState);
    	
    	NotebookEntryState newNotebookEntryState = notebookEntryStateRepository.findUserNotebookEntryStates(gameState.getId()).get(0);
    	assertEquals(newNotebookEntryState.getNotebookContent().get(0).getMailState().getMail().getBody(), "This is inquiry email");
    }
    

    @Test
    public void testCreateUpdatePersonalNote() {
        var text = "Some text for note to test";
        // create 
        var note = notebookService.createPersonalNote(notebookEntryState.getId(),text);
        assertNotNull(note);
        // update        
        NotebookEntryState newNotebookEntryState = notebookEntryStateRepository.findNotebookEntryStateById(notebookEntryState.getId()).orElseThrow();
        note = notebookService.updatePersonalNote(newNotebookEntryState.getNotebookContent().get(0).getId(), "Update note test");
        assertEquals("Update note test", note.getMarkdownContent());

    }
    
    @Test
    public void testGetNotebookAndUploadedFilesForUser () throws Exception {
		Page page = pageRepository.findByTitle("Page 2");
		GameState gameState = gameStateRepository.findByUserId(user.getId()).get(0);
		UploadExercise uploadExercise = new UploadExercise();
		uploadExercise.setPage(page);
		componentRepository.save(uploadExercise);
		
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		var exerciseStates = exerciseStateService.getUserExerciseStates(gameState.getId());
		for(ExerciseState exerciseState : exerciseStates) {
			if (exerciseState.getExercise() instanceof UploadExercise) {
				exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
			}
		}
		uploadExerciseService.uploadFile(file, exerciseStateId);
    	notebookService.initializeStateForNewPage(user);
    	
    	HashMap<String, InputStream> filesInputStream = notebookService.getNotebookAndUploadedFilesForUser(gameState.getId(), user.getId());
    	assertNotNull(filesInputStream);
		assertEquals(filesInputStream.size(), 2);
		
    }
    
    @Test
    public void testGetNotebookAndUploadedFilesForClassrom() throws FileNotFoundException, CreatePdfException, CreateZipException {
    	var testUser = new User(Gender.Male, "Marugoto", "Test", "notebooktest@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
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
    	FileInputStream filesInputStream = notebookService.getCompressedFileForClassroom(users, classroom.getId());
    	assertNotNull(filesInputStream);
    }
    
}
