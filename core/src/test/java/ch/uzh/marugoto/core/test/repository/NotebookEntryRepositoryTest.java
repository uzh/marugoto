package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertNotNull;

public class NotebookEntryRepositoryTest extends BaseCoreTest {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;

    @Autowired
    private PageRepository pageRepository;

    @Test
    public void testFindByPageAndCreationTime() {
        var page = pageRepository.findByTitle("Page 1");
        var notebookEntry = notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), NotebookEntryCreateAt.enter);

        assertNotNull(notebookEntry);
    }
}
