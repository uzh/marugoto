package ch.uzh.marugoto.core.data.entity.state;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;

@Document
@JsonIgnoreProperties({"notebookEntry", "pageState"})
public class PersonalNote {
    @Id
    private String id;
    private String markdownContent;
    private LocalDateTime createdAt;
    @Ref
    private NotebookEntry notebookEntry;
    @Ref
    private PageState pageState;

    @PersistenceConstructor
    public PersonalNote(String markdownContent) {
        super();
        this.markdownContent = markdownContent;
        this.createdAt = LocalDateTime.now();
    }
    
    public PersonalNote(String markdownContent, PageState pageState, NotebookEntry notebookEntry) {
        this(markdownContent);
        this.pageState = pageState;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

	public NotebookEntry getNotebookEntry() {
		return notebookEntry;
	}

	public void setNotebookEntry(NotebookEntry notebookEntry) {
		this.notebookEntry = notebookEntry;
	}

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(PageState pageState) {
        this.pageState = pageState;
    }
}
