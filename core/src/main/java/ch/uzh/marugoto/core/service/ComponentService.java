package ch.uzh.marugoto.core.service;

import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
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
	static final int MATCHING_SCORE = 90;
	static final int FULLY_MATCHED = 0;

	@Autowired
	private ComponentRepository componentRepository;

	/**
	 * Finds exercise by ID
	 * 
	 * @param exerciseId
	 * @return
	 */
	public TextExercise getExercise(String exerciseId) {
		var textExercise = (TextExercise) componentRepository.findById(exerciseId).get();
		return textExercise;
	}

	/**
	 * checks a exercise if its correct and returns if its correct or not
	 * 
	 * @param ExerciseState exerciseState
	 * @return boolean if the exercise was filled in correct or not
	 */

	public boolean isExerciseCorrect(ExerciseState exerciseState) {

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

			if (correct == true) {
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
