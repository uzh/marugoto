package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.DialogSpeechRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;

public class DialogResponseRepositoryTest extends BaseCoreTest {
    @Autowired
    private DialogSpeechRepository dialogSpeechRepository;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;

    @Test
    public void testFindByDialogSpeech() {
        var speech = dialogSpeechRepository.findOne(Example.of(new DialogSpeech("Hey, are you ready for testing?"))).orElse(null);
        assert speech != null;
        assertEquals(2, dialogResponseRepository.findByDialogSpeech(speech.getId()).size());
    }
}
