package ch.uzh.marugoto.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

@Service
public class PageTransitionStateService {

	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private PageTransitionService pageTransitionService;
	@Autowired
	private ExerciseStateService exerciseStateService;

	/**
	 * Creates states for page transitions
	 *
	 * @param pageState
	 */
	public void initializeStateForNewPage(PageState pageState) {
		List<PageTransitionState> pageTransitionStates = new ArrayList<>();

		for (PageTransition pageTransition : pageTransitionService.getAllPageTransitions(pageState.getPage())) {
			var pageTransitionState = new PageTransitionState(pageTransition);
			pageTransitionStates.add(pageTransitionState);
		}
		pageState.setPageTransitionStates(pageTransitionStates);
		pageStateService.savePageState(pageState);
	}

	/**
	 * Updates page transition state according to criteria and exercise
	 *
	 * @param exerciseState
	 * @return stateChanged
	 */
	public PageTransitionState updateAvailabilityForPageTransitionState(ExerciseState exerciseState) {
		PageTransition pageTransition = pageTransitionService.getPageTransition(exerciseState.getExercise().getPage(), exerciseState.getExercise());
		PageState pageState = exerciseState.getPageState();
		PageTransitionState pageTransitionState = getPageTransitionState(pageState, pageTransition);
		boolean satisfied = isExerciseCriteriaSatisfied(pageTransition, exerciseState);

		pageTransitionState.setAvailable(satisfied);

		if (satisfied != pageTransitionState.isAvailable()) {
			pageTransitionState.setStateChanged(true);
		}
		
		pageStateService.savePageState(pageState);
		return pageTransitionState;
	}

	/**
	 * Transition: from page - to page Updates transition state when transition is
	 * in progress
	 *
	 * @param chosenByPlayer
	 * @param pageTransitionId
	 * @param user
	 * @return pageTransition
	 */
	public PageTransition updateOnTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);
		
		PageState pageState = user.getCurrentPageState();
		if (getPageTransitionState(pageState, pageTransition).isAvailable() == false) {
			throw new PageTransitionNotAllowedException();
		}

		TransitionChosenOptions chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
		PageTransitionState pageTransitionState = getPageTransitionState(pageState, pageTransition);
		pageTransitionState.setChosenBy(chosenBy);
		pageStateService.savePageState(pageState);
		return pageTransition;
	}

	/**
	 * Finds page transition state note: it is nested in page state
	 *
	 * @param pageState
	 * @param pageTransition
	 * @return PageTransitionState
	 */
	private PageTransitionState getPageTransitionState(PageState pageState, PageTransition pageTransition) {
		return pageState.getPageTransitionStates().stream().filter(state -> state.getPageTransition().equals(pageTransition)).findFirst().orElseThrow();
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
	 * TODO check how this should be used Checks criteria that depends on the page
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
				satisfied = pageStateList.stream().anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
				break;
			case notVisited:
				satisfied = pageStateList.stream().noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
			}
		}

		return satisfied;
	}
}
