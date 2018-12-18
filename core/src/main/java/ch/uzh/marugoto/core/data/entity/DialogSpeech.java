package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;

/**
 * Dialog speech
 * Belongs to dialog exercise
 */
@Document
public class DialogSpeech {
    @Id
    private String id;
    private String markdownContent;
    @Ref
    private DialogExercise dialogExercise;

    public DialogSpeech() {
        super();
    }

    public DialogSpeech(String markdownContent, DialogExercise dialogExercise) {
        this();
        this.markdownContent = markdownContent;
        this.dialogExercise = dialogExercise;
    }

    public String getId() {
        return id;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public DialogExercise getDialogExercise() {
        return dialogExercise;
    }

    public void setDialogExercise(DialogExercise dialogExercise) {
        this.dialogExercise = dialogExercise;
    }
}
