package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties({"pageState"})
public class PersonalNote {
    @Id
    private String id;
    private String markdownContent;
    @Ref
    private PageState pageState;
    private LocalDateTime createdAt;
    @Ref
    private NotebookEntry notebookEntry;

    @PersistenceConstructor
    public PersonalNote(String markdownContent) {
        super();
        this.markdownContent = markdownContent;
        this.createdAt = LocalDateTime.now();
    }
    
    public PersonalNote(String markdownContent, NotebookEntry notebookEntry) {
        super();
        this.markdownContent = markdownContent;
        this.createdAt = LocalDateTime.now();
        this.notebookEntry = notebookEntry;
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

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(PageState pageState) {
        this.pageState = pageState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public NotebookEntry getNotebookEntry() {
		return notebookEntry;
	}

	public void setNotebookEntry(NotebookEntry notebookEntry) {
		this.notebookEntry = notebookEntry;
	}
}
