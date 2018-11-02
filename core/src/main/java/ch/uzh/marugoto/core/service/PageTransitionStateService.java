package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;

@Service
public class PageTransitionStateService {

	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private PageTransitionService pageTransitionService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private Messages messages;

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
	 * @param user
	 * @return stateChanged if value of isAvailable has been changed
	 */
	public boolean updatePageTransitionStatesAvailability(User user) {
		PageState pageState = user.getCurrentPageState();
		boolean availabilityChanged = false;
		// update transition state availability and update flag if state is changed
		for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
			boolean criteriaSatisfied = isExerciseCriteriaSatisfied(pageTransitionState.getPageTransition(), pageState);

			if (!availabilityChanged) {
				availabilityChanged = pageTransitionState.isAvailable() != criteriaSatisfied;
			}

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
		boolean available = true;

		if (pageTransition.hasCriteria()) {
			// check only if page transition has page criteria
			if (pageTransitionService.hasPageCriteria(pageTransition)) {
				// get user page states
				List<PageState> pageStateList = pageStateService.getPageStates(pageState.getUser());
				available = isPageCriteriaSatisfied(pageTransition, pageStateList);
			}
			// check only if page transition has exercise criteria
			if (pageTransitionService.hasExerciseCriteria(pageTransition)) {
				available = isExerciseCriteriaSatisfied(pageTransition, pageState);
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
	public PageTransition updateOnTransition(TransitionChosenOptions chosenBy, String pageTransitionId, User user) throws PageTransitionNotAllowedException, PageTransitionNotFoundException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);
		PageState pageState = user.getCurrentPageState();
		PageTransitionState pageTransitionState = getPageTransitionState(pageState, pageTransition);

		if (pageTransitionState == null || !pageTransitionState.isAvailable()) {
			throw new PageTransitionNotAllowedException(messages.get("transitionNotAllowed"));
		}

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
		return pageState.getPageTransitionStates().stream().filter(state -> state.getPageTransition().equals(pageTransition)).findFirst().orElse(null);
	}

	/**
	 * Checks criteria that depends on the exercise
	 * correct / not correct / no input
	 *
	 * @param pageTransition
	 * @param pageState
	 * @return
	 */
	public boolean isExerciseCriteriaSatisfied(PageTransition pageTransition, PageState pageState) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForExercise()) {
				ExerciseState exerciseState = exerciseStateService.getExerciseState(criteria.getAffectedExercise(), pageState);
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
			if (criteria.isForPage()) {
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
		}

		return satisfied;
	}
}
