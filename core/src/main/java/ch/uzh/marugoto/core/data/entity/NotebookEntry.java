package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import java.time.LocalDateTime;

@Document
public class NotebookEntry {
    private String id;
    private String title;
    private String text;
    @Ref
    private Page page;
    private NotebookEntryCreationTime notebookEntryCreationTime;
    private LocalDateTime createdAt;


    public NotebookEntry() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public NotebookEntry(Page page, String title, String text) {
        this();
        this.page = page;
        this.title = title;
        this.text = text;
    }

    public NotebookEntry(Page page, String title, String text, NotebookEntryCreationTime notebookEntryCreationTime) {
        this(page, title, text);
        this.title = title;
        this.text = text;
        this.notebookEntryCreationTime = notebookEntryCreationTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public NotebookEntryCreationTime getNotebookEntryCreationTime() {
        return notebookEntryCreationTime;
    }

    public void setNotebookEntryCreationTime(NotebookEntryCreationTime notebookEntryCreationTime) {
        this.notebookEntryCreationTime = notebookEntryCreationTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
