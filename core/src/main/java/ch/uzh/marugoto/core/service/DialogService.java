package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.Dialog;
import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;

@Service
public class DialogService extends NotificationService {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private DialogResponseRepository dialogResponseRepository;

    public List<Dialog> getIncomingDialogs(Page page) {
        return getPageNotifications(page).stream()
                .filter(notification -> notification instanceof Dialog)
                .map(notification -> {
                    var dialog = (Dialog) notification;
                    List<DialogResponse> dialogResponses = getResponsesForDialog(dialog.getSpeech());
                    dialog.setAnswers(dialogResponses);
                    return dialog;
                }).collect(Collectors.toList());
    }

    /**
     * Handler when one of the dialog response is selected
     * adds notebook entry for selected dialog response
     *
     * @param dialogResponseId
     * @param user
     * @return
     */
    public DialogResponse dialogResponseSelected(String dialogResponseId, User user) {
        DialogResponse dialogResponse = dialogResponseRepository.findById(dialogResponseId).orElseThrow();
        notebookService.addNotebookEntryForDialogResponse(user.getCurrentPageState(), dialogResponse);
        return dialogResponse;
    }

    /**
     * Finds answers for dialog
     *
     * @param dialogSpeech
     * @return
     */
    public List<DialogResponse> getResponsesForDialog(DialogSpeech dialogSpeech) {
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
        return nextDialogSpeech;
    }
}
