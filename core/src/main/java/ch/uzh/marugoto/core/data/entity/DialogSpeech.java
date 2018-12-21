package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;

/**
 * Dialog speech
 * Belongs to dialog exercise
 */
@Document
public class DialogSpeech {
    @Id
    private String id;
    private String markdownContent;

    public DialogSpeech() {
        super();
    }

    public DialogSpeech(String markdownContent) {
        this();
        this.markdownContent = markdownContent;
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
}
