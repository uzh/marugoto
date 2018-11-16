package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;

@Document
public class PersonalNote {
    @Id
    private String id;
    private String markdownContent;
    @Ref
    private PageState pageState;
    private LocalDateTime createdAt;

    @PersistenceConstructor
    public PersonalNote(String markdownContent) {
        super();
        this.markdownContent = markdownContent;
        this.createdAt = LocalDateTime.now();
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
}
