package ch.uzh.marugoto.core.data.entity.topic;

public class ExerciseOption {

    private String text;
    private boolean correct;

    public ExerciseOption() {
        super();
    }

    public ExerciseOption(String text, boolean correct) {
        this();
        this.text = text;
        this.correct = correct;
    }

    public ExerciseOption(String text) {
        this();
        this.text = text;
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
