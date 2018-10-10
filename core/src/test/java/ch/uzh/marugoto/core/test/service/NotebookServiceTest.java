package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NotebookServiceTest extends BaseCoreTest {

    @Autowired
    private NotebookService notebookService;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testGetNotebookEntry() {
        var pageState = userRepository.findByMail("unittest@marugoto.ch").getCurrentlyAt();
        var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryCreateAt.enter);

        assertNotNull(notebookEntry);
        assertEquals(pageState.getPage().getTitle(), notebookEntry.getPage().getTitle());
    }
}
