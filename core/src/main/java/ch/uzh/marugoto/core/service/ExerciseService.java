package ch.uzh.marugoto.core.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class ExerciseService extends ComponentService {

	private static final int MATCHING_SCORE = 90;
	private static final int FULLY_MATCHED = 0;

	/**
	 * Returns all the components that belong to page
	 *
	 * @param page
	 * @return components
	 */
	public List<Exercise> getExercises(Page page) {
		return getPageComponents(page).stream().filter(component -> component instanceof Exercise).map(component -> (Exercise) component).collect(Collectors.toList());
	}

	/**
	 * Check whether page has exercise component or not
	 *
	 * @param page Page that has to be checked
	 * @return boolean
	 */
	public boolean hasExercise(Page page) {
		List<Component> components = getPageComponents(page);
		return components.stream().anyMatch(component -> component instanceof Exercise);
	}

	public boolean checkExercise(Exercise exercise, String inputToCheck) {
		boolean correct = false;
		if (exercise instanceof CheckboxExercise) {
			correct = checkExercise((CheckboxExercise) exercise, inputToCheck);
		} else if (exercise instanceof TextExercise){
			correct = checkExercise((TextExercise) exercise, inputToCheck);

		} else if (exercise instanceof RadioButtonExercise){
			correct = checkExercise((RadioButtonExercise) exercise, inputToCheck);

		} else if (exercise instanceof DateExercise){
			correct = checkExercise((DateExercise) exercise, inputToCheck);

		}

		return correct;
	}

	/**
	 * Check if checkbox exercise is correct or not
	 *
	 * @param checkboxExercise
	 * @param inputToCheck
	 * @return boolean if exercise is true or false
	 */
	public boolean checkExercise(CheckboxExercise checkboxExercise, String inputToCheck) {

		boolean correct = false;

		switch (checkboxExercise.getMode()) {
			case minSelection:
				for (var optionIndex : inputToCheck.split(",")) {
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
				for (var optionIndex : inputToCheck.split(",")) {
					var index = Integer.parseInt(optionIndex);
					if (checkboxExercise.getOptions().size() > index) {
						boolean sameSize = checkboxExercise.getMaxSelection().size() == inputToCheck.split(",").length;
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
	 * Check if text box exercise is correct or not
	 *
	 * @param textExercise
	 * @param inputToCheck
	 * @return boolean if exercise is true or false
	 */
	public boolean checkExercise(TextExercise textExercise, String inputToCheck) {

		boolean correct = false;

		for (TextSolution textSolution : textExercise.getTextSolutions()) {
			switch (textSolution.getMode()) {
				case contains:
					correct = inputToCheck.toLowerCase().contains(textSolution.getTextToCompare().toLowerCase());
					break;
				case fullmatch:
					int match = inputToCheck.toLowerCase().compareTo(textSolution.getTextToCompare().toLowerCase());
					correct = match == FULLY_MATCHED;
					break;
				case fuzzyComparison:
					int score = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), inputToCheck);
					correct = score > MATCHING_SCORE;
					break;
			}

			if (correct) {
				break;
			}
		}
		return correct;
	}

	/**
	 * Check if radio-button exercise is correct or not
	 * @param radioButtonExercise
	 * @param inputToCheck
	 * @return isCorrect
	 */
	public boolean checkExercise(RadioButtonExercise radioButtonExercise,  String inputToCheck) {
		Integer inputState = Integer.parseInt(inputToCheck);
		return inputState.equals(radioButtonExercise.getCorrectOption());
	}

	/**
	 * Check if Date exercise is correct or not
	 * @param dateExercise
	 * @param inputToCheck
	 * @return isCorrect
	 */
	public boolean checkExercise(DateExercise dateExercise, String inputToCheck) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime inputDateTime = LocalDateTime.parse(inputToCheck, formatter);
		return inputDateTime.isEqual(dateExercise.getSolution().getCorrectDate());
	}
}
