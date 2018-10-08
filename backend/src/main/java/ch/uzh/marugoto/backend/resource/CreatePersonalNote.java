package ch.uzh.marugoto.backend.resource;

import javax.validation.constraints.NotEmpty;

public class CreatePersonalNote {
    @NotEmpty
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
