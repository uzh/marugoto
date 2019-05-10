package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.resource.ComponentResource;
import ch.uzh.marugoto.core.exception.DateNotValidException;

@Service
public class ExerciseStateService {

	@Autowired
	private ExerciseService exerciseService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private Messages messages;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;

	/**
	 * Find exerciseState by id
	 * 
	 * @param exerciseStateId
	 * @return exerciseState
	 */
	public ExerciseState getExerciseState(String exerciseStateId) {
		return exerciseStateRepository.findById(exerciseStateId).orElseThrow();
	}

	/**
	 * Finds exercise state by page state and exercise
	 *
	 * @param exercise
	 * @param pageState
	 * @return exerciseState
	 */
	public ExerciseState getExerciseState(Exercise exercise, PageState pageState) {
		return exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();
	}
	
	
	/**
	 * Create user exercise state for all exercises on the page
	 *
	 * @param pageState
	 */
	public void initializeStateForNewPage(PageState pageState) {
		for (Exercise exercise : exerciseService.getExercises(pageState.getPage())) {
			ExerciseState newExerciseState = new ExerciseState(exercise);
			newExerciseState.setPageState(pageState);
			exerciseStateRepository.save(newExerciseState);
		}
	}

	/**
	 * Updates exercise state with user input
	 *
	 * @param exerciseStateId
	 * @param inputState
	 * @return ExerciseState
	 * @throws ParseException
	 */
	public ExerciseState updateExerciseState(String exerciseStateId, String inputState) throws DateNotValidException {
		ExerciseState exerciseState = exerciseStateRepository.findById(exerciseStateId).orElseThrow();
		exerciseState.setInputState(validateInput(exerciseState, inputState));
		exerciseStateRepository.save(exerciseState);
		return exerciseState;
	}

	/**
	 * Checks if exercise satisfies criteria
	 *
	 * @param exerciseState
	 * @param criteriaType
	 * @return boolean
	 */
	public boolean exerciseSolved(ExerciseState exerciseState, ExerciseCriteriaType criteriaType) {
		boolean solved = false;

		switch (criteriaType) {
		case noInput:
			solved = exerciseState.getInputState() == null || exerciseState.getInputState().isEmpty();
			break;
		case correctInput:
			solved = exerciseState.getInputState() != null
					&& exerciseService.checkExercise(exerciseState.getExercise(), exerciseState.getInputState());
			break;
		case incorrectInput:
			solved = exerciseState.getInputState() != null
					&& !exerciseService.checkExercise(exerciseState.getExercise(), exerciseState.getInputState());
			break;
		}

		return solved;
	}

	/**
	 * Filter through component resource list and add corresponding state
	 *
	 * @param pageState
	 * @return
	 */
	public List<ComponentResource> getComponentResources(PageState pageState) {
		List<ComponentResource> componentResourceList = componentService.getComponentResources(pageState.getPage());

		for (ComponentResource componentResource : componentResourceList) {
			if (componentResource.isExercise()) {
				componentResource.setState(getExerciseState((Exercise) componentResource.getComponent(), pageState));
			}
		}

		return componentResourceList;
	}

	/**
	 * Validates user input for exercise
	 *
	 * @param exerciseState
	 * @param inputState
	 * @return
	 * @throws DateNotValidException
	 */
	private String validateInput(ExerciseState exerciseState, String inputState) throws DateNotValidException {
		if (exerciseState.getExercise() instanceof DateExercise) {
			DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);
			format.setLenient(false);
			try {
				format.parse(inputState);
			} catch (ParseException e) {
				throw new DateNotValidException(messages.get("date.notValid"));
			}
		}

		return inputState;
	}
}
