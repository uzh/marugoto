package ch.uzh.marugoto.core.data.resource;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;

public class NotebookEntryResource {
    private NotebookEntryState notebookEntryState;
    private List<NotebookContent> notebookContentList;

    public NotebookEntryResource() {
        super();
    }

    public NotebookEntryResource(NotebookEntryState notebookEntryState, List<NotebookContent> notebookContentList) {
        this();
        this.notebookEntryState = notebookEntryState;
        this.notebookContentList = notebookContentList;
    }

    public NotebookEntryState getNotebookEntryState() {
        return notebookEntryState;
    }

    public void setNotebookEntryState(NotebookEntryState notebookEntryState) {
        this.notebookEntryState = notebookEntryState;
    }

    public List<NotebookContent> getNotebookContentList() {
        return notebookContentList;
    }

    public void setNotebookContentList(List<NotebookContent> notebookContentList) {
        this.notebookContentList = notebookContentList;
    }
}
