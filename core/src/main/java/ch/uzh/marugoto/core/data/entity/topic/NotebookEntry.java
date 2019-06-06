package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

@Document
@JsonIgnoreProperties({"page"})
public class NotebookEntry {
    @Id
    private String id;
    private String title;
    @Ref
    private Page page;

    public NotebookEntry() {
    	super();
    }

    public NotebookEntry(String title) {
        this();
        this.title = title;
    }

    public NotebookEntry(Page page, String title) {
        this(title);
        this.page = page;
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

	public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;

        if (o instanceof NotebookEntry) {
            NotebookEntry entry = (NotebookEntry) o;
            equals = id.equals(entry.id);
        }

        return equals;
    }
}
