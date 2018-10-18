package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
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

	@Autowired
	private CriteriaService criteriaService;


	/**
	 * Find/Create new storylineState for the user and
	 * finishes current storylineState if exists
	 * 
	 * @param page
	 * @param user
	 */
	public StorylineState getStorylineState(Page page, User user) {
		StorylineState storylineState = user.getCurrentStorylineState();

		if (page.isStartingStoryline()) {
			boolean newStoryline = storylineState != null && !storylineState.getStoryline().equals(page.getStoryline());

			if (newStoryline) {
				storylineState.setFinishedAt(LocalDateTime.now());
				storylineStateRepository.save(storylineState);
			}

			if (storylineState == null || newStoryline) {
				PageState pageState = getPageState(page, user);
				storylineState = createStorylineState(pageState);
			}
		}

		return storylineState;
	}

	/**
	 * Creates story line state
	 *
	 * @param pageState
	 * @return storylineState
	 */
	private StorylineState createStorylineState(PageState pageState) {
		StorylineState storylineState = null;


		if (pageState.getPage().isStartingStoryline()) {
			storylineState = new StorylineState(pageState.getPage().getStoryline());
			storylineState.setStartedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);

			pageState.getUser().setCurrentStorylineState(storylineState);
			userRepository.save(pageState.getUser());

			pageState.setStorylineState(storylineState);
			pageStateRepository.save(pageState);
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
			pageState.setPageTransitionStates(createPageTransitionStates(page, user));
			pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
			pageStateRepository.save(pageState);


			if (page.isStartingStoryline()) {
				createStorylineState(pageState);
			}

			if (page.hasExercise()) {
				createExerciseStates(pageState);
			}
		}

		user.setCurrentPageState(pageState);
		userRepository.save(user);

		return pageState;
	}

	/**
	 * Finds all user page states
	 *
	 * @param user
	 * @return pageStates
	 */
	public List<PageState> getPageStates(User user) {
		return pageStateRepository.findUserPageStates(user.getId());
	}
	
	/**
	 * Creates page transition states for the page
	 *
	 * @return pageTransitionStates
	 */
	private List<PageTransitionState> createPageTransitionStates(Page page, User user) {
		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		List<PageTransitionState> pageTransitionStates = new ArrayList<>();

		for (PageTransition pageTransition : pageTransitions) {
			var pageTransitionState = new PageTransitionState(pageTransition);
			// TODO
//			pageTransitionState.setAvailable(isPageTransitionAllowed(pageTransition, user));
			pageTransitionStates.add(pageTransitionState);
		}

		return pageTransitionStates;
	}

	public boolean isPageTransitionStateAvailable(PageTransition pageTransition, User user) {
		PageState pageState = getPageState(pageTransition.getFrom(), user);

		PageTransitionState pageTransitionState =  pageState.getPageTransitionStates()
				.stream()
				.filter(state -> state.getPageTransition().equals(pageTransition))
				.findFirst()
				.orElseThrow();

		return pageTransitionState.isAvailable();
	}

	/**
	 * Checks if page transition is allowed for user
	 *
	 * @param pageTransition
	 * @param user
	 * @return allowed
	 */
	private boolean isPageTransitionAllowed(PageTransition pageTransition, User user) {
		boolean allowed = true;

		if (!pageTransition.getCriteria().isEmpty()) {
			for (Criteria criteria : pageTransition.getCriteria()) {
				if (criteria.isForExercise()) {
					PageState pageState = getPageState(pageTransition.getFrom(), user);
					ExerciseState exerciseState = getExerciseState(pageState, criteria.getAffectedExercise());
					allowed = criteriaService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
				}

				if (criteria.isForPage()) {
					List<PageState> pageStates = getPageStates(user);
					allowed = criteriaService.pageCriteriaSatisfied(pageStates, criteria);
				}
			}
		}

		return allowed;
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
				var chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
				pageTransitionState.setChosenBy(chosenBy);
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
	 * @param pageState
	 * @param exercise
	 * @return exerciseState
	 */
	public ExerciseState getExerciseState(PageState pageState, Exercise exercise) {
		return exerciseStateRepository.findUserExerciseState(pageState.getId(), exercise.getId()).orElseThrow();
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

		if (page.hasExercise())
			objectMap.put("exerciseState", getExercisesState(pageState));

		objectMap.put("storylineState", getStorylineState(page, user));
		objectMap.put("pageState", pageState);

		return objectMap;
	}
}
