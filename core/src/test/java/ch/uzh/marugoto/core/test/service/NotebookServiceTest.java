package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookServiceTest extends BaseCoreTest {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
//    @Autowired
//    private PersonalNoteRepository personalNoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    private User user;

    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void testGetUserNotebookEntries() {
        var testEntries = notebookService.getUserNotebookEntries(user);
        assertEquals(2, testEntries.size());
    }

    @Test(expected = Exception.class)
    public void testGetNotebookEntry() {
        var pageState = user.getCurrentPageState();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryAddToPageStateAt.enter).orElse(null);

        assertNotNull(notebookEntry);
        assertEquals(pageState.getPage().getTitle(), notebookEntry.getPage().getTitle());

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

//    @Test
//    public void testCreateUpdateGetDeletePersonalNote() throws PageStateNotFoundException {
//        // create
//        var text = "Some text for note to test";
//        var note = notebookService.createPersonalNote(text, user);
//        assertNotNull(note);
//        // update
//        note = notebookService.updatePersonalNote(note.getId(), "Update note test");
//        assertEquals("Update note test", note.getMarkdownContent());
//        // get
//        var findNote = personalNoteRepository.findById(note.getId()).orElseThrow();
//        assertNotNull(findNote);
//        assertEquals(note.getId(), findNote.getId());
//        // delete
//        notebookService.deletePersonalNote(findNote.getId());
//        var present = personalNoteRepository.findById(findNote.getId()).isPresent();
//        assertFalse(present);
//
//    }
//
//    @Test(expected = PageStateNotFoundException.class)
//    public void testCreatePersonalNoteExceptionIsThrown() throws PageStateNotFoundException {
//        var text = "Some text for note to test";
//        user.setCurrentPageState(null);
//        notebookService.createPersonalNote(text, user);
//    }
}
