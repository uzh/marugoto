package ch.uzh.marugoto.core.data.entity.resource;

public class PersonalNoteRequest implements RequestDto {
    private String markdownContent;

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String note) {
        this.markdownContent = note;
    }
}
