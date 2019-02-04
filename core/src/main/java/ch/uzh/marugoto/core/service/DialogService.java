package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;

@Service
public class DialogService {

    @Autowired
    private DialogResponseRepository dialogResponseRepository;

    public DialogResponse getResponseById(String dialogResponseId) {
        return dialogResponseRepository.findById(dialogResponseId).orElse(null);
    }

    /**
     * Finds answers for dialog speech
     *
     * @param dialogSpeech
     * @return
     */
    public List<DialogResponse> getResponsesForDialogSpeech(DialogSpeech dialogSpeech) {
        return dialogResponseRepository.findByDialogSpeech(dialogSpeech.getId());
    }

    /**
     * Finds next dialog speech with answers from chosen dialog response
     *
     * @param dialogResponse
     * @return nextDialogSpeech
     */
    public DialogSpeech getNextDialogSpeech(DialogResponse dialogResponse) {
        DialogSpeech nextDialogSpeech = dialogResponse.getTo();
        nextDialogSpeech.setAnswers(getResponsesForDialogSpeech(nextDialogSpeech));
        return nextDialogSpeech;
    }
}
