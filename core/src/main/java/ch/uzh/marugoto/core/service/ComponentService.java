package ch.uzh.marugoto.core.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * 
 * Base Service for all components
 *
 */
@Service
public class ComponentService {

	@Autowired
	private ComponentRepository componentRepository;



	static final int MATCHING_SCORE = 90;
	static final int FULLY_MATCHED = 0;

	/**
	 * Returns all the components that belong to page
	 *
	 * @param page
	 * @return components
	 */
	public List<Component> getPageComponents(Page page) {
		return componentRepository.findByPageId(page.getId());
	}

	/**
	 * 
	 * TODO Refactor this method 
	 * 
	 * checks exercise if its correct and returns if its correct or not
	 * 
	 * @param ExerciseState exerciseState
	 * @return boolean if the exercise was filled in correct or not
	 */

	public boolean isExerciseCorrect(ExerciseState exerciseState) {
		
		boolean correct;

		if (exerciseState.getExercise() instanceof CheckboxExercise) {
			correct = isCheckboxExerciseCorrect(exerciseState);
		
		} else if (exerciseState.getExercise() instanceof TextExercise){
			correct = isTextExerciseCorrect(exerciseState);
			
		} else if (exerciseState.getExercise() instanceof RadioButtonExercise){
			correct = isRadioButtonExerciseCorrect(exerciseState);
		
		} else if (exerciseState.getExercise() instanceof DateExercise ){
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
					    correct = checkboxExercise.getMinSelection().stream().
					    		filter(o -> o.getText().contains(checkboxExercise.getOptions().get(index - 1).getText())).findFirst().isPresent();
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
						boolean isPresent = checkboxExercise.getMaxSelection().stream().
					    		filter(o -> o.getText().equals(checkboxExercise.getOptions().get(index - 1).getText())).findFirst().isPresent();				
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
		if (inputState == radioButtonExercise.getCorrectOption()) {
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
	 * Check if textbox exercise is correct or not
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
	
	/**
	 * Converts MarkDown text to html text
	 * 
	 * @param markdownContent
	 * @return
	 */
	public String parseMarkdownToHtml(String markdownText) {
		
		String htmlOutput;
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdownText);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		htmlOutput =  renderer.render(document); 
		return htmlOutput;
	}
}
