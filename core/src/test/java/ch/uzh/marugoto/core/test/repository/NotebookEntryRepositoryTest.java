package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookEntryRepositoryTest extends BaseCoreTest {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void testFindByPageAndCreationTime() {
        var page = pageRepository.findByTitle("Page 1");
        var notebookEntry = notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), NotebookEntryAddToPageStateAt.enter);

        assertNotNull(notebookEntry);
    }

    @Test
    public void testFindUserNotebookEntries() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var notebookEntries = notebookEntryRepository.findUserNotebookEntries(user.getId());

        assertEquals(2, notebookEntries.size());
    }
}
