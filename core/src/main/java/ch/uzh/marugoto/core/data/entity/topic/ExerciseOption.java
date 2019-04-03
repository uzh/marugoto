package ch.uzh.marugoto.core.data.entity.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"correct"})
public class ExerciseOption {

    private String text;
    private boolean correct;

    public ExerciseOption() {
        super();
    }

    public ExerciseOption(String text) {
        this.text = text;
    }

    public ExerciseOption(String text, boolean correct) {
        this();
        this.text = text;
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
