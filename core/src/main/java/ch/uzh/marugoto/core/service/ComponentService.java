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
	 * TextExercise checker - depending of mode it calls 
	 * different comparison method
	 * 
	 * @param textSolutions
	 * @param inputText
	 * @return boolean
	 */
	public boolean checkExercise(TextExercise textExercise, String inputText) {
		List<TextSolution> textSolutions = textExercise.getTextSolutions();

		var solved = false;
		for (TextSolution textSolution : textSolutions) {
			switch (textSolution.getMode()) {
			case contains:
				solved = containsComparisonCheck(textSolution, inputText);
				break;
			case fullmatch:
				solved = fullMatchComparisonCheck(textSolution, inputText);
				break;
			}
			
			if (solved) {
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
	private boolean containsComparisonCheck(TextSolution textSolution, String inputText) {
		return inputText.toLowerCase().contains(textSolution.getTextToCompare().toLowerCase());
	}

	/**
	 * Full match comparison checker - uses FuzzyComparison 
	 * (Levenshtein Distance - https://en.wikipedia.org/wiki/Levenshtein_distance)
	 * @param textSolutions
	 * @param inputText
	 * @return solved
	 */
	private boolean fullMatchComparisonCheck(TextSolution textSolution, String inputText) {
		var solved = false;
		var correct = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), inputText);

		if (correct > 90) {
			solved = true;
		}
		return solved;
	}

	
}
