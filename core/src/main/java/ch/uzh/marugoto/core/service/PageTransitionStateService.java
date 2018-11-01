package ch.uzh.marugoto.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
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
	private ExerciseService exerciseService;
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
			pageTransitionState.setAvailable(isPageTransitionStateAvailable(pageTransition, pageState));
			pageTransitionStates.add(pageTransitionState);
		}
		pageState.setPageTransitionStates(pageTransitionStates);
		pageStateService.savePageState(pageState);
	}

	/**
	 * Updates page transition state according to criteria and exercise
	 *
	 * @param exerciseState User exercise state
	 * @return stateChanged checks value for property isAvailable if previous state is changed
	 */
	public boolean updatePageTransitionStatesAvailability(ExerciseState exerciseState) {
		PageState pageState = exerciseState.getPageState();
		boolean availabilityChanged = false;
		// update transition state availability and update flag if state is changed
		for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
			boolean criteriaSatisfied = isExerciseCriteriaSatisfied(pageTransitionState.getPageTransition(), exerciseState);
			availabilityChanged = pageTransitionState.isAvailable() != criteriaSatisfied;
			pageTransitionState.setAvailable(criteriaSatisfied);
		}

		pageStateService.savePageState(pageState);
		return availabilityChanged;
	}

	/**
	 * Checks all criteria that page transition depends on
	 * Page criteria - check from user page states
	 * Exercise criteria - check from exercise
	 *
	 * @param pageTransition
	 * @param pageState
	 * @return
	 */
	public boolean isPageTransitionStateAvailable(PageTransition pageTransition, PageState pageState) {
		// get user page states
		List<PageState> pageStateList = pageStateService.getPageStates(pageState.getUser());
		boolean available = isPageCriteriaSatisfied(pageTransition, pageStateList);
		// check only if page has exercise
		if (exerciseService.hasExercise(pageState.getPage())) {
			for (Exercise exercise : exerciseService.getExercises(pageState.getPage())) {
				ExerciseState exerciseState = exerciseStateService.getExerciseState(exercise, pageState);
				available = isExerciseCriteriaSatisfied(pageTransition, exerciseState);
			}
		}

		return available;
	}

	/**
	 * Transition: from page - to page Updates transition state when transition is
	 * in progress
	 *
	 * @param chosenBy
	 * @param pageTransitionId
	 * @param user
	 * @return pageTransition
	 */
	public PageTransition updateOnTransition(TransitionChosenOptions chosenBy, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);
		PageState pageState = user.getCurrentPageState();

		if (!getPageTransitionState(pageState, pageTransition).isAvailable()) {
			throw new PageTransitionNotAllowedException();
		}

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
	 * correct / not correct / no input
	 *
	 * @param pageTransition
	 * @param exerciseState
	 * @return
	 */
	public boolean isExerciseCriteriaSatisfied(PageTransition pageTransition, ExerciseState exerciseState) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForExercise()) {
				satisfied = exerciseStateService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
			}
		}

		return satisfied;
	}

	/**
	 * Check if page criteria is satisfied
	 * visited / not visited
	 *
	 * @param pageTransition
	 * @param pageStateList
	 * @return
	 */
	public boolean isPageCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			switch (criteria.getPageCriteria()) {
				case timeExpiration:
					PageState affectedPageState = pageStateList.stream()
							.filter(pageState -> pageState.getPage().equals(criteria.getAffectedPage()))
							.findAny()
							.orElse(null);

					if (affectedPageState != null) {
						satisfied = affectedPageState.getPageTransitionStates().stream()
								.anyMatch(pageTransitionState -> pageTransitionState.getChosenBy().equals(TransitionChosenOptions.autoTransition));
					}
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
