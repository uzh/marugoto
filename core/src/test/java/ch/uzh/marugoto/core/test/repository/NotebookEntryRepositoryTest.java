package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.MailService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

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
    private MailService mailService;

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
        var mail = mailService.getIncomingMails(page6).get(0);
        var notebookEntry = new NotebookEntry(mail, "title", "text");
        notebookEntryRepository.save(notebookEntry);
        var notebookEntryForMail = notebookEntryRepository.findByMailId(mail.getId());

        assertNotNull(notebookEntryForMail);
    }
}




