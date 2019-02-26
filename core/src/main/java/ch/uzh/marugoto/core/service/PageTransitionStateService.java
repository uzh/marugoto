package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;

@Service
public class PageTransitionStateService {

	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private PageTransitionService pageTransitionService;
	@Autowired
	private CriteriaService criteriaService;
	@Autowired
	private Messages messages;

	/**
	 * Creates state list for page transitions
	 * list should have only available transitions sorted by isAvailable property
	 *
	 * @param user
	 */
	public void initializeStateForNewPage(User user) {
		List<PageTransitionState> pageTransitionStates = new ArrayList<>();
		PageState pageState = user.getCurrentPageState();

		for (PageTransition pageTransition : pageTransitionService.getAllPageTransitions(pageState.getPage())) {
			var pageTransitionState = new PageTransitionState(pageTransition);
			pageTransitionState.setAvailable(isPageTransitionStateAvailable(pageTransition, user));
			pageTransitionStates.add(pageTransitionState);
		}

		pageTransitionStates.sort(Comparator.comparing(PageTransitionState::isAvailable)
				.thenComparing(PageTransitionState::getPageTransition, Comparator.comparing(PageTransition::getId)));

		pageStateService.updatePageTransitionStates(pageState, pageTransitionStates);
	}

	/**
	 * Updates page transition state according to criteria and exercise
	 *
	 * @param user
	 * @return stateChanged if value of isAvailable has been changed
	 */
	public boolean checkPageTransitionStatesAvailability(User user) {
		boolean oneOfStatesChanged = false;
		PageState pageState = user.getCurrentPageState();
		// update transition state availability and update flag if state is changed
		for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
			PageTransition pageTransition = pageTransitionState.getPageTransition();
			boolean availableBeforeCheck = pageTransitionState.isAvailable();
			boolean available = isPageTransitionStateAvailable(pageTransition, user);
			pageTransitionState.setAvailable(available);

			if (oneOfStatesChanged == false) {
				// check if change not happened already
				oneOfStatesChanged = available != availableBeforeCheck;
			}
		}

		pageStateService.savePageState(pageState);
		return oneOfStatesChanged;
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
	 * Checks all criteria that page transition depends on
	 * Page criteria - check from user page states
	 * Exercise criteria - check from exercise
	 *
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	private boolean isPageTransitionStateAvailable(PageTransition pageTransition, User user) {
		return criteriaService.checkPageTransitionCriteria(pageTransition, user);
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
}
