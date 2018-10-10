package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {

	@Autowired
	private StorylineStateRepository storylineStateRepository;

	@Autowired
	private PageStateRepository pageStateRepository;

	@Autowired
	private ExerciseStateRepository exerciseStateRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotebookEntryRepository notebookEntryRepository;


	/**
	 * Starts new storylineState for the user and
	 * finishes current storylineState if exists
	 * 
	 * @param pageState
	 * @param user
	 */
	private void createStorylineState(PageState pageState, User user) {
		if (pageState.getPage().getStartsStoryline() != null) {
			// finish current story line if exist
			if (pageState.getPartOf() != null) {
				StorylineState storylineState = pageState.getPartOf();
				storylineState.setFinishedAt(LocalDateTime.now());
				storylineStateRepository.save(storylineState);
			}

			StorylineState storylineState = new StorylineState(pageState.getPage().getStartsStoryline(), user);
			storylineState.setStartedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);

			pageState.setPartOf(storylineState);
			pageStateRepository.save(pageState);
        }
	}

	/**
	 * Finds page state for the page and user
	 * creates new page state if not exist or if it is from previous page
	 * 
	 * @param page
	 * @return pageState
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = user.getCurrentlyAt();

		if (pageState == null || !pageState.getPage().getId().equals(page.getId())) {
			pageState = new PageState(page);
			pageState.setEnteredAt(LocalDateTime.now());
			pageState.setPageTransitionStates(createPageTransitionStates(page));
			pageStateRepository.save(pageState);

			createExerciseStates(pageState);
			createStorylineState(pageState, user);

			user.setCurrentlyAt(pageState);
			userRepository.save(user);
		}

		addPageStateNotebookEntry(pageState, NotebookEntryCreateAt.enter);

		return pageState;
	}
	
	/**
	 * Creates page transition states for the page
	 * TODO add checking if page transition is available for user
	 * @return pageTransitionStates
	 */
	private List<PageTransitionState> createPageTransitionStates(Page page) {
		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		List<PageTransitionState> pageTransitionStates = new ArrayList<>();

		for (PageTransition pageTransition : pageTransitions) {

			var pageTransitionState = new PageTransitionState(true, pageTransition);
			pageTransitionStates.add(pageTransitionState);
		}

		return pageTransitionStates;
	}

	/**
	 * Create user exercise state for all exercises on the page
	 *
	 * @param pageState
	 */
	private void createExerciseStates(PageState pageState) {
		if (pageState.getPage().hasExercise() && getExercisesState(pageState).isEmpty()) {
			// create exercise states
			for (Component component : pageState.getPage().getComponents()) {
				if (component instanceof Exercise) {
					ExerciseState newExerciseState = new ExerciseState((Exercise) component);
					newExerciseState.setPageState(pageState);
					exerciseStateRepository.save(newExerciseState);
				}
			}
		}
	}

	/**
	 * Finds all user exercises states
	 *
	 * @param pageState
	 * @return exerciseStateList
	 */
	public List<ExerciseState> getExercisesState(PageState pageState) {
		return exerciseStateRepository.findUserExerciseStates(pageState.getId());
	}

	/**
	 * Update all the states after page transition is done
	 * 
	 * @param chosenByPlayer
	 * @param pageTransition
	 * @param user
	 */
	public void updateStatesAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
		PageState fromPageState = getPageState(pageTransition.getFrom(), user);
		fromPageState.setLeftAt(LocalDateTime.now());

		// update page transition state
		for( PageTransitionState pageTransitionState : fromPageState.getPageTransitionStates()) {
			if (pageTransitionState.getPageTransition().getId().equals(pageTransition.getId())) {
				pageTransitionState.setChosenByPlayer(chosenByPlayer);
				break;
			}
		}

		addPageStateNotebookEntry(fromPageState, NotebookEntryCreateAt.exit);
		pageStateRepository.save(fromPageState);
	}

	/**
	 * Adds notebook entry to page state if page should
	 * create entry at certain time

	 * @param pageState
	 * @param notebookEntryCreateAt Time when notebook entry should be created (enter / exit)
	 * @return pageState
	 */
	private void addPageStateNotebookEntry(PageState pageState, NotebookEntryCreateAt notebookEntryCreateAt) {
		NotebookEntry notebookEntry = notebookEntryRepository.findNotebookEntryByCreationTime(pageState.getPage().getId(), notebookEntryCreateAt);

		if (notebookEntry != null) {
			pageState.addNotebookEntry(notebookEntry);
			pageStateRepository.save(pageState);
		}
	}

	/**
	 * Updates exercise state with user input
	 * 
	 * @param exerciseStateId
	 * @param inputState
	 * @return ExerciseState
	 */
	public ExerciseState updateExerciseState(String exerciseStateId, String inputState) {
		ExerciseState exerciseState = exerciseStateRepository.findById(exerciseStateId).orElseThrow();
		exerciseState.setInputState(inputState);
		exerciseStateRepository.save(exerciseState);
		return exerciseState;
	}

	/**
	 * Returns all user states for the page
	 * @param page
	 * @param user
	 * @return objectMap
	 */
	public HashMap<String, Object> getAllStates(Page page, User user) {
		var objectMap = new HashMap<String, Object>();

		PageState pageState = getPageState(page, user);

		if (page.hasExercise()) {
			List<ExerciseState> exerciseStates = exerciseStateRepository.findUserExerciseStates(pageState.getId());
			objectMap.put("exerciseState", exerciseStates);
		}

		objectMap.put("storylineState", pageState.getPartOf());
		objectMap.put("pageState", pageState);

		return objectMap;
	}

	ExerciseState getExerciseState(Page from, User user, Exercise exercise) {
		PageState pageState = getPageState(from, user);
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();
		return exerciseState;
	}
}
