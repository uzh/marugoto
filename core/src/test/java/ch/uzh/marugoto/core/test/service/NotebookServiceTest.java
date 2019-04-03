package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.application.User;
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
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.NotebookService;
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
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private NotificationRepository notificationRepository;;
    @Autowired
    private NotebookEntryStateRepository notebookEntryStateRepository;
    @Autowired
    private MailStateRepository mailStateRepository;
    @Autowired
    private GameStateRepository gameStateRepository;
    
    
    private DialogResponse dialogResponse;
    private Mail mail;
    private User user;
    private PageState pageState;
    private GameState gameState;
    private NotebookEntry notebookEntry;
    private NotebookEntryState notebookEntryState;

    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        var dr = new DialogResponse();
        pageState = user.getCurrentPageState();
        dr.setButtonText("Yes");
        dialogResponse = dialogResponseRepository.findOne(Example.of(dr)).orElse(null);
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

        //test getNotebookEntryForDialogResponse
        notebookEntry = new NotebookEntry(dialogResponse, "NotebookEntry");
    	notebookEntryRepository.save(notebookEntry);
    	var notebookEntryForDialog = notebookService.getNotebookEntryForDialogResponse(dialogResponse);
    	assertNotNull(notebookEntryForDialog);
        
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
}













