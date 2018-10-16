package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookServiceTest extends BaseCoreTest {

    @Autowired
    private NotebookService notebookService;

    @Autowired
    private PersonalNoteRepository personalNoteRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("unittest@marugoto.ch");
    }

    @Test
    public void testGetNotebookEntry() {
        var pageState = user.getCurrentPageState();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryCreateAt.enter);

        assertNotNull(notebookEntry);
        assertEquals(pageState.getPage().getTitle(), notebookEntry.getPage().getTitle());
    }

    @Test
    public void testCreateUpdateGetDeletePersonalNote() throws PageStateNotFoundException {
        // create
        var text = "Some text for note to test";
        var note = notebookService.createPersonalNote(text, user);
        assertNotNull(note);
        // update
        note = notebookService.updatePersonalNote(note.getId(), "Update note test");
        assertEquals("Update note test", note.getMarkdownContent());
        // get
        var findNote = notebookService.getPersonalNote(note.getId());
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
        user.setCurrentPageState(null);
        notebookService.createPersonalNote(text, user);
    }
}
