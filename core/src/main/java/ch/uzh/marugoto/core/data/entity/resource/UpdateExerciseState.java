package ch.uzh.marugoto.core.data.entity.resource;

public class UpdateExerciseState implements RequestDto {
    private String inputState;

    public String getInputState() {
        return inputState;
    }

    public void setInputState(String inputState) {
        this.inputState = inputState;
    }
}
