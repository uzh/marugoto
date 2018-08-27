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
	
	public ComponentRepository getRepository() {
		return componentRepository;
	}

	public TextExercise getExercise(String exerciseId) {
		var textExercise = (TextExercise) componentRepository.findById(exerciseId).get();
		return textExercise;
	}
	
	public boolean checkExerciseSolution(TextExercise textExercise, String inputText) {
		List<TextSolution> textSolutions = textExercise.getTextSolutions();

		var solved = false;
		for (var i = 0; i < textSolutions.size() - 1; i++) {
			var correct = FuzzySearch.weightedRatio(textSolutions.get(i).getSolution(), inputText);

			if (correct > 90) {
				solved = true;
				break;
			}
		}
		
		return solved;
	}
}
