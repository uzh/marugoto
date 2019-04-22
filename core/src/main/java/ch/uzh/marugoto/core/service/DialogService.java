package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.Salutation;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.DialogState;
import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.DialogAction;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.DialogStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.helpers.StringHelper;

@Service
public class DialogService {

    @Autowired
    private DialogResponseRepository dialogResponseRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private DialogStateRepository dialogStateRepository;

    /**
     * List of dialogs notifications for the current page
     * set corresponding dialog responses
     *
     * @param user authenticated user
     * @return dialog list
     */
    public List<Dialog> getIncomingDialogs(User user) {
        Page currentPage = user.getCurrentPageState().getPage();
        List<Dialog> dialogList =  notificationRepository.findDialogNotificationsForPage(currentPage.getId()).stream()
                .dropWhile(dialog -> getDialogState(user, dialog.getSpeech()).isPresent())
                .collect(Collectors.toList());

        // TODO filter out answered dialogs
        for (Dialog dialog : dialogList) {
            DialogSpeech dialogSpeech = dialog.getSpeech();
            dialogSpeech.setMarkdownContent(getFormattedText(dialogSpeech.getMarkdownContent(), user));
            List<DialogResponse> dialogResponseList = getResponsesForDialog(dialog.getSpeech());
            dialog.setAnswers(dialogResponseList);
        }

        return dialogList;
    }

    /**
     * Find Dialog state by dialog speech
     *
     * @param user
     * @param dialogSpeech
     * @return
     */
    public Optional<DialogState> getDialogState(User user, DialogSpeech dialogSpeech) {
        return dialogStateRepository.findDialogStateByDialogSpeech(user.getCurrentGameState().getId(), dialogSpeech.getId());
    }

    /**
     * Find Dialog state by dialog response
     *
     * @param user
     * @param dialogResponse
     * @return
     */
    public Optional<DialogState> getDialogState(User user, DialogResponse dialogResponse) {
        return dialogStateRepository.findDialogStateByResponse(user.getCurrentGameState().getId(), dialogResponse.getId());
    }

    /**
     * Handler when one of the dialog response is selected
     *
     * @param dialogResponseId
     * @param user
     * @return
     */
    public DialogResponse dialogResponseSelected(String dialogResponseId, User user) {
        DialogResponse dialogResponse = dialogResponseRepository.findById(dialogResponseId).orElseThrow();
        dialogStateRepository.save(new DialogState(user.getCurrentGameState(), dialogResponse.getFrom(), dialogResponse));

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
        return dialogResponse.getTo();
    }

    public DialogAction getDialogAction(DialogResponse dialogResponse) {
        DialogAction action = DialogAction.none;

        if (dialogResponse.getPageTransition() != null) {
            action = DialogAction.transition;
        } else if (dialogResponse.getTo() != null && false == dialogResponse.getTo().equals(dialogResponse.getFrom())) {
            action = DialogAction.nextDialog;
        }

        return action;
    }

    /**
     * Format dialog speech body text, replace user placeholder
     * with real name
     *
     * @param dialogText
     * @param user authenticated user
     * @return formatted text
     */
	private String getFormattedText(String dialogText, User user) {
        String gender;
        if (user.getSalutation() == Salutation.Mr) {
            gender = Salutation.Sir.name();
        } else {
            gender = Salutation.Madam.name();
        }

        dialogText = StringHelper.replaceInText(dialogText, Constants.NOTIFICATION_GENDER_PLACEHOLDER, gender);
        dialogText = StringHelper.replaceInText(dialogText, Constants.NOTIFICATION_FIRST_NAME_PLACEHOLDER, user.getFirstName());

        return dialogText;
    }
}
