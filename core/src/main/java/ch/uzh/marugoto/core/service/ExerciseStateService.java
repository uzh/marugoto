package ch.uzh.marugoto.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.Option;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.exception.DateNotValidException;

@Service
public class ExerciseStateService {

	@Autowired
	private ExerciseService exerciseService;
	@Autowired
	private Messages messages;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private PageStateRepository pageStateRepository;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private NotebookEntryRepository notebookEntryRepository;

	public ExerciseService getExerciseService() {
		return exerciseService;
	}

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
	 * Finds all page exercise states
	 *
	 * @param pageState
	 * @return exerciseStateList
	 */
	public List<ExerciseState> getAllExerciseStates(PageState pageState) {
		return exerciseStateRepository.findByPageStateId(pageState.getId());
	}

	/**
	 * Finds all users exercise states
	 * 
	 * @param userId
	 * @return
	 */
	public List<ExerciseState> findUserExerciseStates(String userId) {
		List<ExerciseState> exerciseStates = new ArrayList<>();
		var pageStates = pageStateRepository.findUserPageStates(userId);
		for (PageState pageState : pageStates) {
			exerciseStates.addAll(exerciseStateRepository.findByPageStateId(pageState.getId()));
		}
		return exerciseStates;
	}

	/**
	 * Create user exercise state for all exercises on the page
	 *
	 * @param pageState
	 */
	public void initializeStateForNewPage(PageState pageState) {
		if (exerciseService.hasExercise(pageState.getPage())) {
			for (Component component : exerciseService.getPageComponents(pageState.getPage())) {
				if (component instanceof Exercise) {
					ExerciseState newExerciseState = new ExerciseState((Exercise) component);
					newExerciseState.setPageState(pageState);
					exerciseStateRepository.save(newExerciseState);
				}
			}
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
		addStateToNotebookEntry(exerciseState.getExercise(),inputState);
		exerciseStateRepository.save(exerciseState);
		return exerciseState;
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

	/**
	 * Checks if exercise satisfies criteria
	 *
	 * @param exerciseState
	 * @param criteriaType
	 * @return boolean
	 */
	public boolean exerciseCriteriaSatisfied(ExerciseState exerciseState, ExerciseCriteriaType criteriaType) {
		boolean satisfies = false;

		switch (criteriaType) {
		case noInput:
			satisfies = exerciseState.getInputState() == null || exerciseState.getInputState().isEmpty();
			break;
		case correctInput:
			satisfies = exerciseState.getInputState() != null
					&& exerciseService.checkExercise(exerciseState.getExercise(), exerciseState.getInputState());
			;
			break;
		case incorrectInput:
			satisfies = exerciseState.getInputState() != null
					&& !exerciseService.checkExercise(exerciseState.getExercise(), exerciseState.getInputState());
			;
			break;
		}

		return satisfies;
	}

	/**
	 * Search list for exercises and adds corresponding states
	 *
	 * @param components
	 * @param pageState
	 */
	public void addExerciseStates(List<Component> components, PageState pageState) {
		for (Component component : components) {
			if (component instanceof Exercise) {
				var exercise = (Exercise) component;
				exercise.setExerciseState(getExerciseState(exercise, pageState));
			}
		}
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
}
