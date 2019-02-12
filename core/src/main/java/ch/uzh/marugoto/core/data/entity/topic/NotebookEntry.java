package ch.uzh.marugoto.core.data.entity.topic;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.state.PersonalNote;

@Document("notebookEntry")
@JsonIgnoreProperties({"page", "dialogResponse", "mail", "addToPageStateAt"})
public class NotebookEntry {
    @Id
    private String id;
    private String title;
    private String text;
    @Ref
    private Page page;
    @Ref
    private DialogResponse dialogResponse;
    @Ref
    private Mail mail;
    @Transient
    private List<PersonalNote>personalNotes;
    private NotebookEntryAddToPageStateAt addToPageStateAt;

    public NotebookEntry() {
    	super();
    }

    public NotebookEntry(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public NotebookEntry(Page page, String title, String text) {
        super();
        this.page = page;
        this.title = title;
        this.text = text;
    }

    public NotebookEntry(DialogResponse dialogResponse, String title, String text) {
        super();
        this.dialogResponse = dialogResponse;
        this.title = title;
        this.text = text;
    }
    
    public NotebookEntry(Mail mail, String title, String text) {
        super();
        this.mail = mail;
        this.title = title;
        this.text = text;
    }
    
    public NotebookEntry(Page page, String title, String text, NotebookEntryAddToPageStateAt addToPageStateAt) {
        this(page, title, text);
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
    
    public void addText(String text) {
    	if (this.text == null) {
    		setText(text);
    	} else {
    		setText(getText().concat("<br>" + text));
    	}
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

    public NotebookEntryAddToPageStateAt getAddToPageStateAt() {
        return addToPageStateAt;
    }

    public void setAddToPageStateAt(NotebookEntryAddToPageStateAt addToPageStateAt) {
        this.addToPageStateAt = addToPageStateAt;
    }
    
    public void setPersonalNotes(List<PersonalNote>personalNotes) {
    	this.personalNotes = personalNotes;
    }
    
    public List<PersonalNote> getPersonalNotes() {
		return personalNotes;
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
