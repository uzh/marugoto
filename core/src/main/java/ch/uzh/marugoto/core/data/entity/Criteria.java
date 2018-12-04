package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class Criteria {
    private PageCriteriaType pageCriteria;
    private ExerciseCriteriaType exerciseCriteria;
    @Ref
    private Exercise affectedExercise;
    @Ref
    private Page affectedPage;

    public Criteria() {
        super();
    }

    public Criteria(PageCriteriaType pageCriteria, Page affectedPage) {
        this();
        this.pageCriteria = pageCriteria;
        this.affectedPage = affectedPage;
    }

    public Criteria(ExerciseCriteriaType exerciseCriteria, Exercise affectedExercise) {
        this();
        this.exerciseCriteria = exerciseCriteria;
        this.affectedExercise = affectedExercise;
    }

    public Criteria(PageCriteriaType pageCriteria, Page affectedPage, ExerciseCriteriaType exerciseCriteria, Exercise affectedExercise) {
        this();
        this.pageCriteria = pageCriteria;
        this.affectedPage = affectedPage;
        this.exerciseCriteria = exerciseCriteria;
        this.affectedExercise = affectedExercise;
    }

    public PageCriteriaType getPageCriteria() {
        return pageCriteria;
    }

    public ExerciseCriteriaType getExerciseCriteria() {
        return exerciseCriteria;
    }

    public Exercise getAffectedExercise() {
        return affectedExercise;
    }

    public Page getAffectedPage() {
        return affectedPage;
    }

    public boolean isForPage() {
        return this.affectedPage != null;
    }

    public boolean isForExercise() {
        return this.affectedExercise != null;
    }
}
