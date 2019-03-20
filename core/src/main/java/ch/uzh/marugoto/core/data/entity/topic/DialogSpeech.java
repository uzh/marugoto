package ch.uzh.marugoto.core.data.entity.topic;

import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

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
    @Transient
    private List<DialogResponse> answers;

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

    public List<DialogResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<DialogResponse> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DialogSpeech that = (DialogSpeech) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
