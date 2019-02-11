package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookServiceTest extends BaseCoreTest {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PersonalNoteRepository personalNoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private PageStateService pageStateService;
    @Autowired
    private PageStateRepository pageStateRepository;

    private DialogResponse dialogResponse;
    private Mail mail;
    private Page page6;
    private User user;

    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
        var dr = new DialogResponse();
        dr.setButtonText("Yes");
        dialogResponse = dialogResponseRepository.findOne(Example.of(dr)).orElse(null);
        page6 = pageRepository.findByTitle("Page 6");
        mail = mailService.getIncomingMails(page6).get(0);
    }

    @Test
    public void testGetUserNotebookEntries() {
    	var page2 = pageRepository.findByTitle("Page 2");
    	var pageState = pageStateService.initializeStateForNewPage(page2, user);
    	var notebookEntry = new NotebookEntry(page2, "title", "test");
    	notebookEntryRepository.save(notebookEntry);
    	pageState.addNotebookEntry(notebookEntry);
    	pageStateRepository.save(pageState);
    	
        var testEntries = notebookService.getUserNotebookEntries(user);
        assertEquals(3, testEntries.size());
    }

    @Test(expected = Exception.class)
    public void testGetNotebookEntry() {
        var pageState = user.getCurrentPageState();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryAddToPageStateAt.enter).orElse(null);

        assertNotNull(notebookEntry);
        assertEquals(pageState.getPage().getTitle(), notebookEntry.getPage().getTitle());

        //test getNotebookEntryForDialogResponse
        notebookEntry = new NotebookEntry(dialogResponse, "NotebookEntry", "This is notebookEntry for DialogResponse");
    	notebookEntryRepository.save(notebookEntry);
    	var notebookEntryForDialog = notebookService.getNotebookEntryForDialogResponse(dialogResponse);
    	assertNotNull(notebookEntryForDialog);
        
        //test getNotebookEntryForMail
        notebookEntry = new NotebookEntry(mail, "title", "text");
        notebookEntryRepository.save(notebookEntry);
        var notebookEntryForMailExercise = notebookService.getNotebookEntryForMail(mail);
        assertNotNull(notebookEntryForMailExercise);
        
        // expected exception
        var page4 = pageRepository.findByTitle("Page 4");
        notebookService.getNotebookEntry(page4, NotebookEntryAddToPageStateAt.exit).orElseThrow();
    }

    @Test
    public void testAddNotebookEntry() {
        var page = pageRepository.findByTitle("Page 3");
        var pageState = new PageState(pageRepository.findByTitle("Page 3"), user);

        notebookEntryRepository.save(new NotebookEntry(page, "Test entry", "entry text", NotebookEntryAddToPageStateAt.enter));
        notebookService.addNotebookEntry(pageState, NotebookEntryAddToPageStateAt.enter);

        assertNotNull(pageState.getNotebookEntries());
        assertEquals(1, pageState.getNotebookEntries().size());
    }
    
    @Test
    public void testAddNotebookEntryForDialogResponse() {
        var pageState = new PageState(pageRepository.findByTitle("Page 6"), user);
        
        notebookEntryRepository.save(new NotebookEntry(dialogResponse,"notebookEntryforDialogTitle", "notebookEntryforDialogText"));
        notebookService.addNotebookEntryForDialogResponse(pageState, dialogResponse);
        assertNotNull(pageState.getNotebookEntries());
        assertEquals(1, pageState.getNotebookEntries().size());
    }
    
    @Test
    public void testAddNotebookEntryForMail() {
        var pageState = new PageState(pageRepository.findByTitle("Page 6"), user);

        notebookEntryRepository.save(new NotebookEntry(mail, "notebookEntryforMailExericseTitle", "notebookEntryforMailExericseText"));
        notebookService.addNotebookEntryForMail(pageState, mail);
        assertNotNull(pageState.getNotebookEntries());
        assertEquals(1, pageState.getNotebookEntries().size());
    }

    @Test
    public void testCreateUpdateGetDeletePersonalNote() throws PageStateNotFoundException {
        // create
        var text = "Some text for note to test";
        var pageState = user.getCurrentPageState();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryAddToPageStateAt.enter).orElse(null);
        var note = notebookService.createPersonalNote(notebookEntry.getId(),text, user);
        assertNotNull(note);
        // update
        note = notebookService.updatePersonalNote(note.getId(), "Update note test");
        assertEquals("Update note test", note.getMarkdownContent());
        // get
        var findNote = personalNoteRepository.findById(note.getId()).orElseThrow();
        assertNotNull(findNote);
        assertEquals(note.getId(), findNote.getId());
        // delete
        notebookService.deletePersonalNote(findNote.getId());
        var present = personalNoteRepository.findById(findNote.getId()).isPresent();
        assertFalse(present);

    }

    @Test(expected = PageStateNotFoundException.class)
    public void testCreatePersonalNoteExceptionIsThrown() throws PageStateNotFoundException {
        var text = "Some text for note to test";
        var page = pageRepository.findByTitle("Page 1");
        user.setCurrentPageState(null);
        var notebookEntry = notebookService.getNotebookEntry(page, NotebookEntryAddToPageStateAt.enter).orElse(null);
        notebookService.createPersonalNote(notebookEntry.getId(),text, user);
    }
}
