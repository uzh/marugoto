package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
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
    public List<DialogResponse> getResponseForDialogSpeech(DialogSpeech dialogSpeech) {
        return dialogResponseRepository.findByDialogSpeech(dialogSpeech.getId());
    }
}
