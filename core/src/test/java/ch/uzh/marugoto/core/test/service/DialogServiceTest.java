package ch.uzh.marugoto.core.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.DialogSpeechRepository;
import ch.uzh.marugoto.core.service.DialogService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;


public class DialogServiceTest extends BaseCoreTest {

    @Autowired
    private DialogService dialogService;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private DialogSpeechRepository dialogSpeechRepository;
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
    }

    @Test
    public void testGetResponseById() {
        var dialogResponseId = dialogResponseRepository.findAll().iterator().next().getId();
        assertEquals(dialogResponseId, dialogService.getResponseById(dialogResponseId).getId());
    }

    @Test
    public void testGetResponsesForDialogSpeech() {
        assertEquals(2, dialogService.getResponsesForDialogSpeech(speech1).size());
        assertEquals(1, dialogService.getResponsesForDialogSpeech(speech2).size());
    }

    @Test
    public void testGetNextDialogSpeech() {
        assertEquals(speech2.getId(), dialogService.getNextDialogSpeech(response1).getId());
        assertEquals(speech3.getId(), dialogService.getNextDialogSpeech(response2).getId());
    }
}
