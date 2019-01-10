package ch.uzh.marugoto.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.MailExercise;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;

@Service
public class FakeEmailService {

	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired 
	private ExerciseStateService exerciseStateService;
	
	public List<MailExercise> getAllMailExercises(String userId) {

		List<MailExercise> mailExercises = new ArrayList<>();
		List<ExerciseState> exerciseStates = exerciseStateService.findUserExerciseStates(userId);
		for (ExerciseState exerciseState : exerciseStates) {
			if (exerciseState.getExercise() instanceof MailExercise) {
				mailExercises.add((MailExercise) exerciseState.getExercise());
			}
		}
		return mailExercises;
	}

	public Exercise getMailExerciseById(String pageStateId, String exerciseId) {
		Exercise mailExercise = null;
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(pageStateId, exerciseId).orElseThrow();
		if (exerciseState.getExercise() instanceof MailExercise) {
			mailExercise = exerciseState.getExercise();
		}
		return mailExercise;
	}
	
	public ExerciseState sendEmail(String pageStateId, String mailExerciseId) {
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(pageStateId, mailExerciseId).orElseThrow();
        if (exerciseState.getExercise() instanceof MailExercise) {
        	String mailBody = ((MailExercise) exerciseState.getExercise()).getMailBody();
        	exerciseState.setInputState(mailBody);
        	exerciseStateRepository.save(exerciseState);
        }
        return exerciseState;
	}
}