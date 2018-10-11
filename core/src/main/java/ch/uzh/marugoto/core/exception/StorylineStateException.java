package ch.uzh.marugoto.core.exception;

import ch.uzh.marugoto.core.data.entity.Page;

public class StorylineStateException extends Exception {
    private static final long serialVersionUID = 1L;

    private StorylineStateException(String message) {
        super(message);
    }

    public StorylineStateException(Page page) {
        this("Error in creating storyline state: " + page.getTitle() + " doesn\'t start storyline");
    }
}
