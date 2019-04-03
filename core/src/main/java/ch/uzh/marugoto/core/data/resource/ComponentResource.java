package ch.uzh.marugoto.core.data.resource;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;

public class ComponentResource {
    @Ref
    private Component component;
    @Ref
    private ExerciseState state;

    public ComponentResource(Component component) {
        this.component = component;
    }

    @JsonIgnoreProperties({ "id", "page" })
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ExerciseState getState() {
        return state;
    }

    public void setState(ExerciseState state) {
        this.state = state;
    }

    @JsonIgnore
    public boolean isExercise() {
        return component instanceof Exercise;
    }

    @JsonGetter
    public String getType() {
        return component.getClass().getSimpleName();
    }
}
