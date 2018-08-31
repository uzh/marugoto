package ch.uzh.marugoto.core.service;

import ch.uzh.marugoto.core.data.entity.TextExercise;

public interface TextExerciseChecker {

	public boolean checkExercise(TextExercise textExercise, String inputText);
}
