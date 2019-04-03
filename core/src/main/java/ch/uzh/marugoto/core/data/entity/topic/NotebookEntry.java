package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

@Document
@JsonIgnoreProperties({"page", "dialogResponse", "mail"})
public class NotebookEntry {
    @Id
    private String id;
    private String title;
    @Ref
    private Page page;
    @Ref
    private DialogResponse dialogResponse;
    @Ref
    private Mail mail;

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

    public NotebookEntry(DialogResponse dialogResponse, String title) {
        this(title);
        this.dialogResponse = dialogResponse;
    }
    
    public NotebookEntry(Mail mail, String title) {
        this(title);
        this.mail = mail;
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

    public DialogResponse getDialogResponse() {
        return dialogResponse;
    }

    public void setDialogResponse(DialogResponse dialogResponse) {
        this.dialogResponse = dialogResponse;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
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
