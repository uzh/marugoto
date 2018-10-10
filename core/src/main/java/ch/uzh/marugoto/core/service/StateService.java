package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
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
	private NotebookService notebookService;


	/**
	 * Find/Create new storylineState for the user and
	 * finishes current storylineState if exists
	 * 
	 * @param page
	 * @param user
	 */
	private StorylineState getStorylineState(Page page, User user) {
		StorylineState storylineState = user.getCurrentlyPlaying();

		if (page.getStartsStoryline()) {
			boolean newStoryline = storylineState != null && !storylineState.getStoryline().equals(page.getStoryline());

			if (newStoryline) {
				storylineState.setFinishedAt(LocalDateTime.now());
				storylineStateRepository.save(storylineState);
			}

			if (storylineState == null || newStoryline) {
				storylineState = new StorylineState(page.getStoryline());
				storylineState.setStartedAt(LocalDateTime.now());
				storylineStateRepository.save(storylineState);

				user.setCurrentlyPlaying(storylineState);
				userRepository.save(user);
			}
		}

		return storylineState;
	}

	/**
	 * Finds page state for the page and user
	 * creates new page state if not exist or if it is from previous page
	 * 
	 * @param page
	 * @return pageState
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageId(page.getId(), user.getId());

		if (pageState == null) {
			pageState = new PageState(page, user);
			pageState.setEnteredAt(LocalDateTime.now());
			pageState.setPageTransitionStates(createPageTransitionStates(page));
			pageState.setBelongsTo(user);
			pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
			pageStateRepository.save(pageState);

			createExerciseStates(pageState);

			user.setCurrentlyAt(pageState);
			userRepository.save(user);
		}

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
		if (pageState.getPage().hasExercise()) {
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
	 * Finds all page exercise states
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
		fromPageState.addNotebookEntry(notebookService.getNotebookEntry(fromPageState.getPage(), NotebookEntryCreateAt.exit));
		fromPageState.setLeftAt(LocalDateTime.now());

		// update page transition state
		for( PageTransitionState pageTransitionState : fromPageState.getPageTransitionStates()) {
			if (pageTransitionState.getPageTransition().equals(pageTransition)) {
				pageTransitionState.setChosenByPlayer(chosenByPlayer);
				break;
			}
		}

		pageStateRepository.save(fromPageState);

		PageState nextPageState = getPageState(pageTransition.getTo(), user);
		nextPageState.addNotebookEntry(notebookService.getNotebookEntry(nextPageState.getPage(), NotebookEntryCreateAt.enter));
		pageStateRepository.save(nextPageState);
	}

	/**
	 * Finds exercise state by page state and exercise
	 *
	 * @param from
	 * @param user
	 * @param exercise
	 * @return
	 */
	public ExerciseState getExerciseState(Page from, User user, Exercise exercise) {
		PageState pageState = getPageState(from, user);
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();
		return exerciseState;
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
			objectMap.put("exerciseState", getExercisesState(pageState));
		}

		objectMap.put("storylineState", getStorylineState(page, user));
		objectMap.put("pageState", pageState);

		return objectMap;
	}

	public List<PageState> getPageStates(User user) {
		return pageStateRepository.findUserPageStates(user.getId());
	}
}
