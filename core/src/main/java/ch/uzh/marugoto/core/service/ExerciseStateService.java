package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;

@Service
public class ExerciseStateService {
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private ExerciseService exerciseService;

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
        return exerciseStateRepository.findByPageStateId(pageState.getId());
    }

    /**
     * Create user exercise state for all exercises on the page
     *
     * @param pageState
     */
    void createExerciseStates(PageState pageState) {
        if (exerciseService.hasExercise(pageState.getPage())) {
            for (Component component : exerciseService.getPageComponents(pageState.getPage())) {
                if (component instanceof Exercise) {
                    ExerciseState newExerciseState = new ExerciseState((Exercise) component);
                    newExerciseState.setPageState(pageState);
                    exerciseStateRepository.save(newExerciseState);
                }
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
}
