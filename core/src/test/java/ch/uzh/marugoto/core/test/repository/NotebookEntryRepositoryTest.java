package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NotebookEntryRepositoryTest extends BaseCoreTest {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void testFindByPageAndCreationTime() {
        var page = pageRepository.findByTitle("Page 1");
        var notebookEntry = notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), NotebookEntryAddToPageStateAt.enter);

        assertNotNull(notebookEntry);
    }

    @Test
    public void testFindUserNotebookEntries() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var notebookEntries = notebookEntryRepository.findUserNotebookEntries(user.getCurrentGameState().getId());

        assertEquals(2, notebookEntries.size());
        assertThat(notebookEntries.get(0).getTitle(), is("Page 1 entry"));
    }
    
    @Test
    public void testFindNotebookEntryByDialogResponse() {
    	var dialogResponse = new DialogResponse();
    	dialogResponse.setButtonText("Yes");
        var dialogResponse1 = dialogResponseRepository.findOne(Example.of(dialogResponse)).orElse(null);
    	var notebookEntry = new NotebookEntry(dialogResponse1, "NotebookEntry", "This is notebookEntry for DialogResponse");
    	notebookEntryRepository.save(notebookEntry);
    	var notebookEntryForDialogResponse = notebookEntryRepository.findNotebookEntryByDialogResponse(dialogResponse1.getId()).orElseThrow();
    	
    	assertNotNull(notebookEntryForDialogResponse);
    }
    
    @Test
    public void testFindNotebookEntryByMail() {
        var page6 = pageRepository.findByTitle("Page 6");
        var mails = notificationRepository.findMailNotificationsForPage(page6.getId());
        var notebookEntry = new NotebookEntry(mails.get(0), "title", "text");
        notebookEntryRepository.save(notebookEntry);
        var notebookEntryForMail = notebookEntryRepository.findByMailId(mails.get(0).getId());

        assertNotNull(notebookEntryForMail);
    }
}




