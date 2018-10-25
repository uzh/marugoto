package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class ExerciseStateService {
	
    private static final int MATCHING_SCORE = 90;
    private static final int FULLY_MATCHED = 0;
	
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

    /**
     *
     * TODO Refactor this method
     *
     * checks exercise if its correct and returns if its correct or not
     *
     * @param exerciseState Exercise state
     * @return boolean if the exercise was filled in correct or not
     */

    boolean isExerciseCorrect(ExerciseState exerciseState) {

        boolean correct;

        if (exerciseState.getExercise() instanceof CheckboxExercise) {
            correct = isCheckboxExerciseCorrect(exerciseState);

        } else if (exerciseState.getExercise() instanceof TextExercise){
            correct = isTextExerciseCorrect(exerciseState);

        } else if (exerciseState.getExercise() instanceof RadioButtonExercise){
            correct = isRadioButtonExerciseCorrect(exerciseState);

        } else if (exerciseState.getExercise() instanceof DateExercise){
            correct = isDateExerciseCorrect(exerciseState);

        } else {
            correct = false;
        }
        return correct;
    }

    /**
     * Check if checkbox exercise is correct or not
     *
     * @param exerciseState
     * @return boolean if exercise is true or false
     */
    public boolean isCheckboxExerciseCorrect (ExerciseState exerciseState) {

        boolean correct = false;
        CheckboxExercise checkboxExercise = (CheckboxExercise) exerciseState.getExercise();

        switch (checkboxExercise.getMode()) {
            case minSelection:
                for (var optionIndex : exerciseState.getInputState().split(",")) {
                    var index = Integer.parseInt(optionIndex);
                    if (checkboxExercise.getOptions().size() >= index) {
                        correct = checkboxExercise.getMinSelection()
                                .stream()
                                .anyMatch(o -> o.getText().contains(checkboxExercise.getOptions().get(index - 1).getText()));
                        if (correct) {
                            break;
                        }
                    }
                }
                break;
            case maxSelection:
                for (var optionIndex : exerciseState.getInputState().split(",")) {
                    var index = Integer.parseInt(optionIndex);
                    if (checkboxExercise.getOptions().size() > index) {
                        boolean sameSize = checkboxExercise.getMaxSelection().size() == exerciseState.getInputState().split(",").length;
                        boolean isPresent = checkboxExercise.getMaxSelection().stream()
                                .anyMatch(o -> o.getText().equals(checkboxExercise.getOptions().get(index - 1).getText()));
                        correct = sameSize && isPresent;
                        if (!correct) {
                            break;
                        }
                    }
                }
        }
        return correct;
    }

    /**
     * Check if radio-button exercise is correct or not
     * @param exerciseState
     * @return
     */
    public boolean isRadioButtonExerciseCorrect (ExerciseState exerciseState) {

        boolean correct = false;
        RadioButtonExercise radioButtonExercise = (RadioButtonExercise) exerciseState.getExercise();
        Integer inputState = Integer.parseInt(exerciseState.getInputState());
        if (inputState.equals(radioButtonExercise.getCorrectOption())) {
            correct = true;
        }

        return correct;
    }

    /**
     * Check if Date exercise is correct or not
     * @param exerciseState
     * @return
     */
    public boolean isDateExerciseCorrect (ExerciseState exerciseState) {
        boolean correct = false;

        DateExercise dateExercise = (DateExercise) exerciseState.getExercise();
        String inputState = exerciseState.getInputState();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime inputDateTime = LocalDateTime.parse(inputState,formatter);

        if (inputDateTime.isEqual(dateExercise.getSolution().getCorrectDate()))
            correct = true;

        return correct;
    }

    /**
     * Check if text box exercise is correct or not
     *
     * @param exerciseState
     * @return boolean if exercise is true or false
     */
    public boolean isTextExerciseCorrect(ExerciseState exerciseState) {

        TextExercise textExercise = (TextExercise) exerciseState.getExercise();
        List<TextSolution> textSolutions = textExercise.getTextSolutions();

        boolean correct = false;
        for (TextSolution textSolution : textSolutions) {
            switch (textSolution.getMode()) {
                case contains:
                    correct = exerciseState.getInputState().toLowerCase().contains(textSolution.getTextToCompare().toLowerCase());
                    break;
                case fullmatch:
                    int match = exerciseState.getInputState().toLowerCase().compareTo(textSolution.getTextToCompare().toLowerCase());
                    if (match == FULLY_MATCHED) {
                        correct = true;
                    }
                    break;
                case fuzzyComparison:
                    int score = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), exerciseState.getInputState());
                    if (score > MATCHING_SCORE) {
                        correct = true;
                    }
                    break;
            }

            if (correct) {
                break;
            }
        }
        return correct;
    }
    
}
