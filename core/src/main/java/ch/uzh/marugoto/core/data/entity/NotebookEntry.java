package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

@Document
@JsonIgnoreProperties({"page"})
public class NotebookEntry {
    @Id
    private String id;
    private String title;
    private String text;
    @Ref
    private Page page;
    private NotebookEntryAddToPageStateAt addToPageStateAt;

    @PersistenceConstructor
    public NotebookEntry(Page page, String title, String text) {
        super();
        this.page = page;
        this.title = title;
        this.text = text;
    }

    public NotebookEntry(Page page, String title, String text, NotebookEntryAddToPageStateAt addToPageStateAt) {
        this(page, title, text);
        this.title = title;
        this.text = text;
        this.addToPageStateAt = addToPageStateAt;
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

    public NotebookEntryAddToPageStateAt getAddToPageStateAt() {
        return addToPageStateAt;
    }

    public void setAddToPageStateAt(NotebookEntryAddToPageStateAt addToPageStateAt) {
        this.addToPageStateAt = addToPageStateAt;
    }
}
