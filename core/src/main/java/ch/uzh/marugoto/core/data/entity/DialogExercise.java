package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * Dialog exercise on page
 * If placeholder text is not defined, dialog is shown immediately
 * Requires speaker and DialogSpeech
 */
public class DialogExercise extends Exercise {

    private String buttonText;
    @Ref
    private Character speaker;
    @Ref
    private DialogSpeech speech;
    @Transient
    private List<DialogResponse> asnwers;

    public DialogExercise() {
        super();
    }

    public DialogExercise(int numberOfColumns, Page page) {
        super(numberOfColumns, page);
    }

    public DialogExercise(int numberOfColumns, Page page, String buttonText, Character speaker, DialogSpeech dialogSpeech) {
        this(numberOfColumns, page);
        this.buttonText = buttonText;
        this.speaker = speaker;
        this.speech = dialogSpeech;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isShownOnPageLoad() {
        return buttonText == null;
    }

    public Character getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Character speaker) {
        this.speaker = speaker;
    }

    public DialogSpeech getSpeech() {
        return speech;
    }

    public void setSpeech(DialogSpeech speech) {
        this.speech = speech;
    }

    public List<DialogResponse> getAsnwers() {
        return asnwers;
    }

    public void setAsnwers(List<DialogResponse> asnwers) {
        this.asnwers = asnwers;
    }
}
