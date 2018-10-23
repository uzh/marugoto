package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;

@Service
public class ExerciseService extends ComponentService {

    @Autowired
    private ExerciseStateRepository exerciseStateRepository;


    /**
     * Returns all the components that belong to page
     *
     * @param page
     * @return components
     */
    public List<Exercise> getExercises(Page page) {
        return getPageComponents(page)
                .stream()
                .filter(component -> component instanceof Exercise)
                .map(component -> (Exercise) component)
                .collect(Collectors.toList());
    }

    /**
     * Finds exercise state by page state and exercise
     *
     * @param exercise
     * @param pageState
     * @return exerciseState
     */
    public ExerciseState getExerciseState(Exercise exercise, PageState pageState) {
        return exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();
    }

    /**
     * Finds all page exercise states
     *
     * @param pageState
     * @return exerciseStateList
     */
    public List<ExerciseState> getAllExerciseStates(PageState pageState) {
        return exerciseStateRepository.findAllUserExerciseStates(pageState.getId());
    }

    /**
     * Create user exercise state for all exercises on the page
     *
     * @param pageState
     */
    void createExerciseStates(PageState pageState) {
        for (Component component : getPageComponents(pageState.getPage())) {
            if (component instanceof Exercise) {
                ExerciseState newExerciseState = new ExerciseState((Exercise) component);
                newExerciseState.setPageState(pageState);
                exerciseStateRepository.save(newExerciseState);
            }
        }
    }

    /**
     * Updates exercise state with user input
     *
     * @param exerciseStateId
     * @param inputState
     * @return ExerciseState
     */
    public ExerciseState updateExerciseState(String exerciseStateId, String inputState) {
        ExerciseState exerciseState = exerciseStateRepository.findById(exerciseStateId).orElseThrow();
        exerciseState.setInputState(inputState);
        exerciseStateRepository.save(exerciseState);
        return exerciseState;
    }

    /**
     * Checks if exercise satisfies criteria
     *
     * @param exerciseState
     * @param exerciseCriteriaType
     * @return boolean
     */
    boolean exerciseCriteriaSatisfied(ExerciseState exerciseState, ExerciseCriteriaType exerciseCriteriaType) {

        boolean satisfies = false;

        switch (exerciseCriteriaType) {
            case noInput:
                satisfies = exerciseState.getInputState() == null || exerciseState.getInputState().isEmpty();
                break;
            case correctInput:
                satisfies = exerciseState.getInputState() != null && isExerciseCorrect(exerciseState);
                break;
            case incorrectInput:
                satisfies = exerciseState.getInputState() != null && !isExerciseCorrect(exerciseState);
                break;
        }

        return satisfies;
    }


}
