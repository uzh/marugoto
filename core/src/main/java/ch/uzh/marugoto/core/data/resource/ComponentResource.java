package ch.uzh.marugoto.core.data.resource;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.topic.Component;

public class ComponentResource {
    @Ref
    private Component component;
    @Ref
    private ExerciseState state;

    public ComponentResource(Component component) {
        this.component = component;
    }

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

    @JsonGetter
    public String getType() {
        return component.getClass().getSimpleName();
    }
}
