package ch.uzh.marugoto.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.backend.data.entity.TextExercise;
import ch.uzh.marugoto.backend.data.entity.TextSolution;
import ch.uzh.marugoto.backend.data.repository.ComponentRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * 
 * Service for exercises 
 *
 */
public class ExerciseService {
	
	@Autowired
	private ComponentRepository componentRepository;
	
	public TextExercise getExercise(String exerciseId) {
		TextExercise textExercise = (TextExercise) componentRepository.findById(exerciseId).get();
		return textExercise;
	}
	
	public float checkExerciseSolution(String exerciseId, String inputText) {
		List<TextSolution> textSolutions = this.getExercise(exerciseId).getTextSolutions();
		
		var correct = 0;
		for (var i = 0; i < textSolutions.size() - 1; i++) {
			correct = FuzzySearch.ratio(textSolutions.get(i).getSolution(), inputText);
		}
		
		return correct;
	}
}
