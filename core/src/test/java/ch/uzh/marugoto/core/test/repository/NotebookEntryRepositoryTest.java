package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class NotebookEntryRepositoryTest extends BaseCoreTest {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void testFindByPageAndCreationTime() {
        var page = pageRepository.findByTitle("Page 1");
        var notebookEntry = notebookEntryRepository.findNotebookEntryByPage(page.getId());

        assertNotNull(notebookEntry);
    }
    
    
    @Test
    public void testFindNotebookEntryByMail() {
        var page6 = pageRepository.findByTitle("Page 6");
        var mails = notificationRepository.findMailNotificationsForPage(page6.getId());
        var notebookEntry = new NotebookEntry(mails.get(0), "title");
        notebookEntryRepository.save(notebookEntry);
        var notebookEntryForMail = notebookEntryRepository.findByMailId(mails.get(0).getId());

        assertNotNull(notebookEntryForMail);
    }
}




