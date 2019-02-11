package ch.uzh.marugoto.core.test.repository;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.PersonalNote;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PersonalNoteRepositoryTest extends BaseCoreTest {

    @Autowired
    private PersonalNoteRepository personalNoteRepository;
    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSave() {
        var text = "test note";
        var personalNote = personalNoteRepository.save(new PersonalNote(text));
        assertNotNull(personalNote);
        assertEquals(text, personalNote.getMarkdownContent());
    }
    
    @Test
    public void testFindByNotebookEntryId() {
    	var page = pageRepository.findByTitle("Page 1");
    	var user = userRepository.findByMail("unittest@marugoto.ch");
        var notebookEntry = notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), NotebookEntryAddToPageStateAt.enter);
        var personalNotes = personalNoteRepository.findAllPersonalNotes(notebookEntry.get().getId(), user.getCurrentPageState().getId());

        assertThat(personalNotes.size(), is(1));
    }
}
