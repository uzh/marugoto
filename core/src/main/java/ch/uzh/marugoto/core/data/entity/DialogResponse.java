package ch.uzh.marugoto.core.data.entity;


import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.Ref;
import com.arangodb.springframework.annotation.To;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

/**
 * Answer for DialogSpeech which can point to another speech
 * or can trigger page transition
 */
@Edge
@JsonIgnoreProperties({"from", "to", "pageTransition"})
public class DialogResponse {
    @Id
    private String id;
    private String buttonText;
    @From
    private DialogSpeech from;
    @To
    private DialogSpeech to;
    @Ref
    private PageTransition pageTransition;

    public DialogResponse() {
        super();
    }

    public DialogResponse(DialogSpeech from, DialogSpeech to, String buttonText) {
        this();
        this.from = from;
        this.to = to;
        this.buttonText = buttonText;
    }

    public DialogResponse(DialogSpeech from, DialogSpeech to, String buttonText, PageTransition pageTransition) {
        this(from, to, buttonText);
        this.pageTransition = pageTransition;
    }

    public String getId() {
        return id;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public DialogSpeech getFrom() {
        return from;
    }

    public void setFrom(DialogSpeech from) {
        this.from = from;
    }

    public DialogSpeech getTo() {
        return to;
    }

    public void setTo(DialogSpeech to) {
        this.to = to;
    }

    public PageTransition getPageTransition() {
        return pageTransition;
    }

    public void setPageTransition(PageTransition pageTransition) {
        this.pageTransition = pageTransition;
    }
}
