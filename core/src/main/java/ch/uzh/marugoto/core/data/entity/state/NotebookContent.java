package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Id;

import ch.uzh.marugoto.core.data.entity.topic.Component;

@Document
@JsonPropertyOrder({"id", "type", "description"})
public class NotebookContent {
    @Id
    private String Id;
    @Ref
    @JsonIgnoreProperties({"numberOfColumns", "offsetColumns", "renderOrder", "showInNotebookAt", "shownInNotebook", "zoomable"})
    private Component component;
    @Ref
    private ExerciseState exerciseState;
    @Ref
    private MailState mailState;
    private PersonalNote personalNote;
    private String description;

    public NotebookContent() {
        super();
    }

    public NotebookContent(Component component) {
        this();
        this.component = component;
    }

    public NotebookContent(PersonalNote personalNote) {
        this.personalNote = personalNote;
    }

    public String getId() {
        return Id;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ExerciseState getExerciseState() {
        return exerciseState;
    }

    public void setExerciseState(ExerciseState exerciseState) {
        this.exerciseState = exerciseState;
    }

    public PersonalNote getPersonalNote() {
        return personalNote;
    }

    public void setPersonalNote(PersonalNote personalNote) {
        this.personalNote = personalNote;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter
    public String getType() {
        String type = null;
        if (component != null) {
            type = component.getClass().getSimpleName();
        } else if (personalNote != null) {
            type = personalNote.getClass().getSimpleName();
        } else if (exerciseState != null) {
            type = exerciseState.getExercise().getClass().getSimpleName();
        }
        return type;
    }
}
