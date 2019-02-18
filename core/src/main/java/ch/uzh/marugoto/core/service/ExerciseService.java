package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextSolution;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class ExerciseService {

	@Autowired
	private ComponentRepository componentRepository;

	/**
	 * Returns all the components that belong to page
	 *
	 * @param page
	 * @return components
	 */
	public List<Exercise> getExercises(Page page) {
		return componentRepository.findPageComponents(page.getId()).stream()
				.filter(component -> component instanceof Exercise).map(component -> (Exercise) component).collect(Collectors.toList());
	}

	/**
	 * Check whether page has exercise component or not
	 *
	 * @param page Page that has to be checked
	 * @return boolean
	 */
	public boolean hasExercise(Page page) {
		List<Component> components = componentRepository.findPageComponents(page.getId());
		return components.stream().anyMatch(component -> component instanceof Exercise);
	}

	/**
	 * Exercise check
	 *
	 * @param exercise
	 * @param inputToCheck
	 * @return
	 */
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
		else if (exercise instanceof UploadExercise){
			correct = checkExercise((UploadExercise) exercise, inputToCheck);
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
		for (var optionIndex : inputToCheck.split(",")) {
			var index = Integer.parseInt(optionIndex);
			correct = checkboxExercise.getOptions().get(index).isCorrectOption();
			if (correct == false) {
				break;
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
					correct = match == Constants.TEXT_EXERCISE_FULLY_MATCHED_SCORE;
					break;
				case fuzzyComparison:
					int score = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), inputToCheck);
					correct = score > Constants.TEXT_EXERCISE_PASSED_SCORE;
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
		return radioButtonExercise.getOptions().get(inputState).isCorrectOption();
	}

	/**
	 * Check if Date exercise is correct or not
	 * @param dateExercise
	 * @param inputToCheck
	 * @return isCorrect
	 */
	public boolean checkExercise(DateExercise dateExercise, String inputToCheck) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
		LocalDate inputDateTime = LocalDate.parse(inputToCheck, formatter);
		return inputDateTime.isEqual(dateExercise.getSolution().getCorrectDate());
	}
	
	public boolean checkExercise (UploadExercise uploadExercise, String inputToCheck) {
		boolean correct = true;
		if (uploadExercise.isMandatory() && inputToCheck.isEmpty()) {
			correct = false;
		} 
		return correct;
	}
}
