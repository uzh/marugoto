package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;

@Service
public class CriteriaService {

    @Autowired
    private ComponentService componentService;

    /**
     * Checks if exercise satisfies criteria
     *
     * @param exerciseState
     * @param criteria
     * @return satisfies
     */
    public boolean exerciseCriteriaSatisfied(ExerciseState exerciseState, ExerciseCriteriaType criteria) {
        boolean satisfies = false;

        switch (criteria) {
            case noInput:
                satisfies = exerciseState.getInputState() == null || exerciseState.getInputState().isEmpty();
                break;
            case correctInput:
                satisfies = exerciseState.getInputState() != null && componentService.isExerciseCorrect(exerciseState);
                break;
            case incorrectInput:
                satisfies = exerciseState.getInputState() != null && !componentService.isExerciseCorrect(exerciseState);
                break;
        }

        return satisfies;
    }

    public boolean pageCriteriaSatisfied(List<PageState> pageStates, Criteria criteria) {
        boolean satisfied = false;

        switch (criteria.getPageCriteria()) {
            case timeExpiration:
                // TODO check how this should be checked
                break;
            case visited:
                satisfied = pageStates
                        .stream()
                        .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
                break;
            case notVisited:
                satisfied = pageStates
                        .stream()
                        .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
        }

        return satisfied;

    }
}
