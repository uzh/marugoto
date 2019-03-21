package ch.uzh.marugoto.core.data.entity.state;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ch.uzh.marugoto.core.Constants;

public class PersonalNote {

    private String markdownContent;
    private LocalDateTime createdAt;

    public PersonalNote() {
        super();
    }

    public PersonalNote(String markdownContent) {
        this();
        this.markdownContent = markdownContent;
        this.createdAt = LocalDateTime.now();
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public String getCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_WITH_TIME));
    }
}
