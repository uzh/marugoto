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
    private NotebookEntryCreateAt createAt;
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

    public NotebookEntry(Page page, String title, String text, NotebookEntryCreateAt createAt) {
        this(page, title, text);
        this.title = title;
        this.text = text;
        this.createAt = createAt;
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

    public NotebookEntryCreateAt getCreateAt() {
        return createAt;
    }

    public void setCreateAt(NotebookEntryCreateAt createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
