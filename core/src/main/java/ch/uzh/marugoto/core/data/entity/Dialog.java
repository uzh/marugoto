package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * Dialog modal on page
 * If timer is not defined, dialog is shown immediately
 * Requires speaker and DialogSpeech
 */
public class Dialog extends Notification {
    @Ref
    private DialogSpeech speech;
    @Transient
    private List<DialogResponse> answers;

    public Dialog() {
        super();
    }

    public Dialog(VirtualTime timer, Page page, Character from) {
        super(timer, page, from);
    }

    public Dialog(VirtualTime timer, Page page, Character from, DialogSpeech dialogSpeech) {
        super(timer, page, from);
        this.speech = dialogSpeech;
    }

    public DialogSpeech getSpeech() {
        return speech;
    }

    public void setSpeech(DialogSpeech speech) {
        this.speech = speech;
    }

    public List<DialogResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<DialogResponse> answers) {
        this.answers = answers;
    }
}
