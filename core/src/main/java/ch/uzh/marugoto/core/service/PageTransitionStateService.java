package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
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
	 * Finds page transition state
	 * note: it is nested in page state
	 *
	 * @param pageState
	 * @param pageTransition
	 * @return
	 */
	private PageTransitionState getPageTransitionState(PageState pageState, PageTransition pageTransition) {
		return pageState.getPageTransitionStates()
				.stream()
				.filter(state -> state.getPageTransition().equals(pageTransition))
				.findFirst()
				.orElseThrow();
	}
	
	/**
	 * Updates page transition state according to criteria and exercise
	 *
	 * @param exerciseState
	 * @return stateChanged
	 */
	public PageTransitionState updateTransitionState(ExerciseState exerciseState) {
		PageTransition pageTransition = pageTransitionService.getPageTransition(exerciseState.getExercise().getPage(), exerciseState.getExercise());
		PageTransitionState pageTransitionState = getPageTransitionState(exerciseState.getPageState(), pageTransition);
		boolean satisfied = isExerciseCriteriaSatisfied(pageTransition, exerciseState);

		pageTransitionState.setAvailable(satisfied);

		if (satisfied != pageTransitionState.isAvailable()) {
			pageTransitionState.setStateChanged(true);
		}

		pageStateRepository.save(exerciseState.getPageState());
		return pageTransitionState;
	}

	/**
	 * Updates chosenBy property
	 *
	 * @param pageState
	 * @param pageTransition
	 * @param chosenBy
	 */
	private void updateTransitionState(PageState pageState, PageTransition pageTransition, TransitionChosenOptions chosenBy) {
		PageTransitionState pageTransitionState = getPageTransitionState(pageState, pageTransition);
		pageTransitionState.setChosenBy(chosenBy);
		pageStateRepository.save(pageState);
	}

	/**
	 * Transition: from page - to page
	 * Updates transition state when transition is in progress
	 *
	 * @param chosenByPlayer
	 * @param pageTransitionId
	 * @param user
	 * @return nextPage
	 */
	public PageTransition updateAfterTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);

		if (!isTransitionAvailable(pageTransition, user)) {
			throw new PageTransitionNotAllowedException();
		}

		PageState fromPageState = user.getCurrentPageState();
		fromPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(fromPageState);

		var chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
		updateTransitionState(fromPageState, pageTransition, chosenBy);


		return pageTransition;
	}

	/**
	 * Checks weather transition is available or not
	 *
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	private boolean isTransitionAvailable(PageTransition pageTransition, User user) throws PageTransitionNotAllowedException {
		try {
			PageState pageState = user.getCurrentPageState();

			if (pageState == null) {
				throw new PageStateNotFoundException();
			}

		    return getPageTransitionState(pageState, pageTransition).isAvailable();
		} catch (PageStateNotFoundException e) {
			throw new PageTransitionNotAllowedException(e.getMessage());
		}
	}

	/**
	 * Checks criteria that depends on the exercise
	 *
	 * @param pageTransition
	 * @param exerciseState
	 * @return
	 */
	private boolean isExerciseCriteriaSatisfied(PageTransition pageTransition, ExerciseState exerciseState) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForExercise()) {
				satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
			}
		}

		return satisfied;
	}

    /**
	 * TODO check how this should be used
     * Checks criteria that depends on the page
     *
     * @param pageTransition
     * @param pageStateList
     * @return
     */
    private boolean isPageCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
        boolean satisfied = false;

        for (Criteria criteria : pageTransition.getCriteria()) {
            switch (criteria.getPageCriteria()) {
                case timeExpiration:
                    // TODO check how this should be checked
                    break;
                case visited:
                    satisfied = pageStateList
                            .stream()
                            .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
                    break;
                case notVisited:
                    satisfied = pageStateList
                            .stream()
                            .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
            }
        }

        return satisfied;
    }
}
