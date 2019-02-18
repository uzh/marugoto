package ch.uzh.marugoto.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.ComponentResource;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.topic.Option;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.exception.DateNotValidException;

@Service
public class ExerciseStateService {

	@Autowired
	private ExerciseService exerciseService;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private Messages messages;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private NotebookEntryRepository notebookEntryRepository;

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
		//addStateToNotebookEntry(exerciseState.getExercise(),inputState);
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
	 * @param componentsResources
	 * @param pageState
	 * @return
	 */
	public List<ComponentResource> addComponentResourceState(List<ComponentResource> componentsResources, PageState pageState) {
		return componentsResources.stream().peek(componentResource -> {
			if (componentResource.getComponent() instanceof Exercise) {
				exerciseStateRepository.findUserExerciseState(pageState.getId(), componentResource.getComponent().getId())
						.ifPresent(componentResource::setState);
			}
		}).collect(Collectors.toList());
	}

	public void addStateToNotebookEntry(Exercise exercise, String inputState) {
		NotebookEntry notebookEntry = notebookService.getNotebookEntry(exercise.getPage(), NotebookEntryAddToPageStateAt.enter).orElseThrow();

		if (exercise instanceof RadioButtonExercise) {
			Option opt = ((RadioButtonExercise) exercise).getOptions().get(Integer.parseInt(inputState));
			notebookEntry.addText(opt.getText());

		} else if (exercise instanceof CheckboxExercise) {
			Option opt = ((CheckboxExercise) exercise).getOptions().get(Integer.parseInt(inputState));
			notebookEntry.addText(opt.getText());
		} else {
			notebookEntry.addText(inputState);
		}

		if (notebookEntry.getTitle().isEmpty()) {
			notebookEntry.setTitle(exercise.getPage().getTitle());
		}
		notebookEntryRepository.save(notebookEntry);
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
