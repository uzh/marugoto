package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.state.DialogState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.DialogSpeechRepository;
import ch.uzh.marugoto.core.data.repository.DialogStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.DialogService;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;


public class DialogServiceTest extends BaseCoreTest {

    @Autowired
    private DialogService dialogService;
    @Autowired
    private NotebookService notebookService;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private DialogSpeechRepository dialogSpeechRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private PageStateRepository pageStateRepository;
    @Autowired
    private DialogStateRepository dialogStateRepository;
    private DialogSpeech speech1;
    private DialogSpeech speech2;
    private DialogSpeech speech3;
    private DialogResponse response1;
    private DialogResponse response2;

    public synchronized void before() {
        super.before();
        speech1 = dialogSpeechRepository.findOne(Example.of(new DialogSpeech("Hey, are you ready for testing?"))).orElse(null);
        speech2 = dialogSpeechRepository.findOne(Example.of(new DialogSpeech("Alright, concentrate then!"))).orElse(null);
        speech3 = dialogSpeechRepository.findOne(Example.of(new DialogSpeech("Then, goodbye!"))).orElse(null);
        var r1 = new DialogResponse();
        r1.setButtonText("Yes");
        response1 = dialogResponseRepository.findOne(Example.of(r1)).orElse(null);
        var r2 = new DialogResponse();
        r2.setButtonText("No");
        response2 = dialogResponseRepository.findOne(Example.of(r2)).orElse(null);
        dialogStateRepository.save(new DialogState());

    }

    @Test
    public void testGetIncomingDialogs() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var page3 = pageRepository.findByTitle("Page 3");
        var pageState = pageStateRepository.save(new PageState(page3, user.getCurrentGameState()));
        user.setCurrentPageState(pageState);
        userRepository.save(user);
        var dialogs = dialogService.getIncomingDialogs(user);
        assertEquals(1, dialogs.size());
    }

    @Test
    public void testDialogResponseSelected() {
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var dialogResponseId = dialogResponseRepository.findAll().iterator().next().getId();
        var notebookList = notebookService.getUserNotebookEntries(user);
        assertEquals(2, notebookList.size());
        dialogService.dialogResponseSelected(dialogResponseId, user);
        assertEquals(3, notebookService.getUserNotebookEntries(user).size());

    }

    @Test
    public void testGetResponsesForDialog() {
        assertEquals(2, dialogService.getResponsesForDialog(speech1).size());
        assertEquals(1, dialogService.getResponsesForDialog(speech2).size());
    }

    @Test
    public void testGetNextDialogSpeech() {
        assertEquals(speech2.getId(), dialogService.getNextDialogSpeech(response1).getId());
        assertEquals(speech3.getId(), dialogService.getNextDialogSpeech(response2).getId());
    }
}
