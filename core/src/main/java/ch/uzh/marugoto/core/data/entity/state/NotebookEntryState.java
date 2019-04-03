package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;

@Document
@JsonPropertyOrder({"id", "title", "notebookContent"})
@JsonIgnoreProperties({ "gameState", "createdAt", "notebookEntry"})
public class NotebookEntryState {
    @Id
    private String id;
    @Ref
    private GameState gameState;
    @Ref
    private NotebookEntry notebookEntry;
    @Ref
    private List<NotebookContent> notebookContent;
    private LocalDateTime createdAt;

    public NotebookEntryState() {
        super();
        this.createdAt = LocalDateTime.now();
        notebookContent = new ArrayList<>();
    }

    public NotebookEntryState(GameState gameState, NotebookEntry notebookEntry) {
        this();
        this.gameState = gameState;
        this.notebookEntry = notebookEntry;
    }

    public String getId() {
        return id;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public NotebookEntry getNotebookEntry() {
        return notebookEntry;
    }

    public void setNotebookEntry(NotebookEntry notebookEntry) {
        this.notebookEntry = notebookEntry;
    }

    public List<NotebookContent> getNotebookContent() {
        return notebookContent;
    }

    public void setNotebookContent(List<NotebookContent> notebookContent) {
        this.notebookContent = notebookContent;
    }

    public void addNotebookContent(NotebookContent notebookContent) {
        this.notebookContent.add(notebookContent);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonGetter
    public String getTitle() {
        String title = null;
        if (notebookEntry != null) {
            title = notebookEntry.getTitle();
        }
        return title;
    }
}
