package ch.uzh.marugoto.core.data.entity;


import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.Ref;
import com.arangodb.springframework.annotation.To;

import org.springframework.data.annotation.Id;

/**
 * Answer for DialogSpeech which can point to another speech
 * or can trigger page transition
 */
@Edge
public class DialogResponse {
    @Id
    private String id;
    @From
    private DialogSpeech from;
    @To
    private DialogSpeech to;
    @Ref
    private PageTransition pageTransition;

    public DialogResponse() {
        super();
    }

    public DialogResponse(DialogSpeech from, DialogSpeech to) {
        this();
        this.from = from;
        this.to = to;
    }

    public DialogResponse(DialogSpeech from, DialogSpeech to, PageTransition pageTransition) {
        this(from, to);
        this.pageTransition = pageTransition;
    }

    public String getId() {
        return id;
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
