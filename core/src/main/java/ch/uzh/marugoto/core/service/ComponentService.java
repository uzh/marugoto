package ch.uzh.marugoto.core.service;

import java.util.List;

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

	@Autowired
	private ComponentRepository componentRepository;
	
	static final int MATCHING_SCORE = 90;
	static final int FULLY_MATCHED = 0;


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
	 * @param ExerciseState exerciseState
	 * @return boolean
	 */
	
	public boolean checkExercise(ExerciseState exerciseState) {

		TextExercise textExercise = (TextExercise)exerciseState.getExercise();
 		List<TextSolution> textSolutions = textExercise.getTextSolutions();

		boolean correct = false;
		for (TextSolution textSolution : textSolutions) {
			switch (textSolution.getMode()) {
				case contains:
					correct = exerciseState.getInputText().toLowerCase().contains(textSolution.getTextToCompare().toLowerCase());
					break;
				case fullmatch:
					int match = exerciseState.getInputText().toLowerCase().compareTo(textSolution.getTextToCompare().toLowerCase());
					if (match == FULLY_MATCHED) {
						correct = true;
					}
					break;
				case fuzzyComparison:
					int score = FuzzySearch.weightedRatio(textSolution.getTextToCompare(), exerciseState.getInputText());
					if (score > MATCHING_SCORE) {
						correct = true;
						break;
					}
				if (correct) {
					break;
				}
			}
		}
		return correct;
	}
	
}
