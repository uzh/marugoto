package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;

@Document
public class DialogState {
    @Id
    private String Id;
    @Ref
    private User user;
    @Ref
    private DialogSpeech dialogSpeech;
    @Ref
    private DialogResponse dialogResponse;
    private LocalDateTime createdAt;

    public DialogState() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public DialogState(User user, DialogSpeech dialogSpeech, DialogResponse dialogResponse) {
        this();
        this.user = user;
        this.dialogSpeech = dialogSpeech;
        this.dialogResponse = dialogResponse;
    }

    public String getId() {
        return Id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DialogSpeech getDialogSpeech() {
        return dialogSpeech;
    }

    public void setDialogSpeech(DialogSpeech dialogSpeech) {
        this.dialogSpeech = dialogSpeech;
    }

    public DialogResponse getDialogResponse() {
        return dialogResponse;
    }

    public void setDialogResponse(DialogResponse dialogResponse) {
        this.dialogResponse = dialogResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
