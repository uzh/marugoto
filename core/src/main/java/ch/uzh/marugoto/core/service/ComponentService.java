package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	 * TextExercise checker - depending of mode it calls different comparison
	 * checkers
	 * 
	 * @param textSolutions
	 * @param inputText
	 * @return
	 */
	public boolean checkTextExercise(List<TextSolution> textSolutions, String inputText) {

		var solved = false;
		for (TextSolution textSolution : textSolutions) {
			switch (textSolution.getMode()) {
			case contains:
				solved = this.containsComparisonCheck(textSolutions, inputText);
				break;
			case fullmatch:
				solved = this.fullMatchComparisonCheck(textSolutions, inputText);
				break;
			}
		}

		return solved;
	}

	/**
	 * Checks if text solution contains given text
	 * 
	 * @param textSolutions
	 * @param inputText
	 * @return
	 */
	private boolean containsComparisonCheck(List<TextSolution> textSolutions, String inputText) {
		var solved = false;
		for (TextSolution textSolution : textSolutions) {
			solved = textSolution.getTextToCompare().toLowerCase().contains(inputText.toLowerCase());
		}

		return solved;
	}

	/**
	 * Fullmatch comparison checker - uses FuzzyComparison 
	 * (Levenshtein Distance - https://en.wikipedia.org/wiki/Levenshtein_distance)
	 * @param textSolutions
	 * @param inputText
	 * @return
	 */
	private boolean fullMatchComparisonCheck(List<TextSolution> textSolutions, String inputText) {
		var solved = false;
		for (TextSolution textSolution : textSolutions) {
			var correct = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), inputText);

			if (correct > 90) {
				solved = true;
				break;
			}
		}
		return solved;
	}
}
