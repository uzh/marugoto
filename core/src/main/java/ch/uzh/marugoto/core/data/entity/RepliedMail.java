package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

@Document()
@JsonIgnoreProperties({ "pageState", })
public class RepliedMail {
    @Id
    private String id;
    private String text;
    @Ref
    private Mail mail;
    @Ref
    private PageState pageState;

    public RepliedMail() {
        super();
    }

    public RepliedMail(Mail mail, PageState pageState) {
        this();
        this.mail = mail;
        this.pageState = pageState;
    }

    public RepliedMail(Mail mail, PageState pageState, String text) {
        this(mail, pageState);
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(PageState pageState) {
        this.pageState = pageState;
    }
}
