package ch.uzh.marugoto.core.data.entity;

public class Criteria {
    private PageCriteriaType pageCriteria;
    private ExerciseCriteriaType exerciseCriteria;
    private Exercise exerciseAffected;
    private Page pageAffected;

    private Criteria() {
        super();
    }

    public Criteria(PageCriteriaType pageCriteria, Page pageAffected) {
        this();
        this.pageCriteria = pageCriteria;
        this.pageAffected = pageAffected;
    }

    public Criteria(ExerciseCriteriaType exerciseCriteria, Exercise exerciseAffected) {
        this();
        this.exerciseCriteria = exerciseCriteria;
        this.exerciseAffected = exerciseAffected;
    }

    public Criteria(PageCriteriaType pageCriteria, Page pageAffected, ExerciseCriteriaType exerciseCriteria, Exercise exerciseAffected) {
        this();
        this.pageCriteria = pageCriteria;
        this.pageAffected = pageAffected;
        this.exerciseCriteria = exerciseCriteria;
        this.exerciseAffected = exerciseAffected;
    }

    public PageCriteriaType getPageCriteria() {
        return pageCriteria;
    }

    public ExerciseCriteriaType getExerciseCriteria() {
        return exerciseCriteria;
    }

    public Exercise getExerciseAffected() {
        return exerciseAffected;
    }

    public Page getPageAffected() {
        return pageAffected;
    }

    public boolean isForPage() {
        return this.pageAffected != null;
    }

    public boolean isForExercise() {
        return this.exerciseAffected != null;
    }
}
