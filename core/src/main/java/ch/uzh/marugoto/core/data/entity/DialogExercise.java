package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

/**
 * Dialog exercise on page
 * If placeholder text is not defined, dialog is shown immediately
 * Requires Speaker and DialogSpeech
 */
public class DialogExercise extends Exercise {

    private String placeholderText;
    private boolean showOnPageLoad;
    private Character speaker;
    @Ref
    private DialogSpeech speech;

    public DialogExercise() {
        super();
    }

    public DialogExercise(String placeholderText, Character speaker, DialogSpeech dialogSpeech) {
        this();
        this.placeholderText = placeholderText;
        this.speaker = speaker;
        this.speech = dialogSpeech;
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public boolean isShownOnPageLoad() {
        return placeholderText == null;
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
}
