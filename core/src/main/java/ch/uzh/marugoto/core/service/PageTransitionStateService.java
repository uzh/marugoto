package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

@Service
public class PageTransitionStateService extends StateService {

	@Autowired
	private PageTransitionService pageTransitionService;

	private NotebookService notebookService;
	@Autowired
	private StorylineStateService storylineStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;

	/**
	 * Creates states for page transitions
	 *
	 * @param pageState
	 */
	public void initializeState(PageState pageState) {
		List<PageTransitionState> pageTransitionStates = new ArrayList<>();

		for (PageTransition pageTransition : pageTransitionService.getAllPageTransitions(pageState.getPage())) {
			var pageTransitionState = new PageTransitionState(pageTransition);
			pageTransitionStates.add(pageTransitionState);
		}

		pageState.setPageTransitionStates(pageTransitionStates);
		pageStateRepository.save(pageState);
	}
	
	/**
	 * Updates page transition state according to criteria and exercise
	 *
	 * @param exerciseState
	 * @return stateChanged
	 */
	public boolean updateTransitionAvailability(ExerciseState exerciseState) {
		boolean stateChanged = false;
		Exercise exercise = exerciseState.getExercise();
		PageTransition pageTransition = pageTransitionService.getPageTransition(exercise.getPage(), exercise);

		if (pageTransition != null) {
			boolean isAvailable = isStateAvailable(exerciseState.getPageState(), pageTransition);
			boolean satisfied = isCriteriaSatisfied(pageTransition, exerciseState);
			updateState(exerciseState.getPageState(), pageTransition, satisfied);

			stateChanged = isAvailable != satisfied;
		}

		return stateChanged;
	}

	/**
	 * Transition: from page - to page Updates previous page states and returns next
	 * page
	 *
	 * @param chosenByPlayer
	 * @param pageTransitionId
	 * @param user
	 * @return nextPage
	 */
	public Page doPageTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);

		try {
			if (!isTransitionAvailable(pageTransition, user))
				throw new PageTransitionNotAllowedException();

			PageState currentPageState = updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
			notebookService.addNotebookEntry(currentPageState, NotebookEntryCreateAt.exit);

			if (user.getCurrentStorylineState() != null) {
				storylineStateService.updateMoneyAndTimeBalance(pageTransition.getMoney(), pageTransition.getVirtualTime(), user.getCurrentStorylineState());
				storylineStateService.finishStoryline(user);
			}

		} catch (PageStateNotFoundException e) {
			throw new PageTransitionNotAllowedException(e.getMessage());
		}

		return pageTransition.getTo();
	}

	/**
	 * Checks weather transition is available or not
	 *
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	private boolean isTransitionAvailable(PageTransition pageTransition, User user) throws PageStateNotFoundException {
		PageState pageState = user.getCurrentPageState();

		if (pageState == null)
			throw new PageStateNotFoundException();

		return isStateAvailable(pageState, pageTransition);
	}

	/**
	 * Checks criteria that depends on the exercise
	 *
	 * @param pageTransition
	 * @param exerciseState
	 * @return
	 */
	private boolean isCriteriaSatisfied(PageTransition pageTransition, ExerciseState exerciseState) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForExercise()) {
				satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
			}
		}

		return satisfied;
	}
	
	/**
	 * Return if page state is available for transition
	 *
	 * @param pageState
	 * @param pageTransition
	 * @return
	 */
	private boolean isStateAvailable(PageState pageState, PageTransition pageTransition) {
		return pageState.getPageTransitionStates().stream().filter(state -> state.getPageTransition().equals(pageTransition)).findFirst().orElseThrow().isAvailable();
	}


	/**
	 * Updates isAvailable property
	 *
	 * @param pageState
	 * @param pageTransition
	 * @param available
	 */
	private void updateState(PageState pageState, PageTransition pageTransition, boolean available) {
		for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
			if (pageTransitionState.getPageTransition().equals(pageTransition)) {
				pageTransitionState.setAvailable(available);
				break;
			}
		}

		pageStateRepository.save(pageState);
	}

	/**
	 * Updates chosenBy property
	 *
	 * @param pageState
	 * @param pageTransition
	 * @param chosenBy
	 */
	private void updateState(PageState pageState, PageTransition pageTransition, TransitionChosenOptions chosenBy) {
		for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
			if (pageTransitionState.getPageTransition().equals(pageTransition)) {
				pageTransitionState.setChosenBy(chosenBy);
				break;
			}
		}

		pageStateRepository.save(pageState);
	}

	/**
	 * Update all the states after page transition is done
	 *
	 * @param chosenByPlayer
	 * @param pageTransition
	 * @param user
	 * @return fromPageState
	 */
	private PageState updateStatesAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
		PageState fromPageState = user.getCurrentPageState();
		fromPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(fromPageState);

		var chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
		updateState(fromPageState, pageTransition, chosenBy);

		return fromPageState;
	}

// TODO is this code needed or can it be removed?
//    /**
//     * Checks criteria that depends on the page
//     *
//     * @param pageTransition
//     * @param pageStateList
//     * @return
//     */
//    private boolean isCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
//        boolean satisfied = false;
//
//        for (Criteria criteria : pageTransition.getCriteria()) {
//            switch (criteria.getPageCriteria()) {
//                case timeExpiration:
//                    // TODO check how this should be checked
//                    break;
//                case visited:
//                    satisfied = pageStateList
//                            .stream()
//                            .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
//                    break;
//                case notVisited:
//                    satisfied = pageStateList
//                            .stream()
//                            .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
//            }
//        }
//
//        return satisfied;
//    }

}
