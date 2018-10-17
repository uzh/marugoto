package ch.uzh.marugoto.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

/**
 * PageService provides functionality related to page and pageTransition entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Autowired
	private StateService stateService;

	@Autowired
	private ComponentService componentService;

	/**
	 * Get page with the user allowed page transitions
	 * 
	 * @param id
	 * @param user
	 * @return Page
	 */
	public Page getPage(String id, User user) {
        Page page = pageRepository.findById(id).orElseThrow();
        page.setPageTransitions(getAllowedPageTransitions(page, user));
		return page;
	}

	/**
	 * Transition: from page - to page
	 * Updates previous page states and returns next page
	 *
	 * @param chosenByPlayer
	 * @param pageTransitionId
	 * @param user
	 * @return nextPage
	 */
	public Page doTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionRepository.findById(pageTransitionId).orElseThrow();

		if (!isPageTransitionAllowed(pageTransition, user)) {
			throw new PageTransitionNotAllowedException();
		}

		Page nextPage = pageTransition.getTo();
		stateService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
		nextPage.setPageTransitions(getAllowedPageTransitions(nextPage, user));

		return nextPage;
	}

	/**
	 * Returns all page transitions
	 *
	 * @param page
	 * @return pageTransitions
	 */
	private List<PageTransition> getPageTransitions(Page page) {
		return pageTransitionRepository.findByPageId(page.getId());
	}

	/**
	 * Returns allowed page transitions for user
	 *
	 * @param page
	 * @param user
	 * @return allowedPageTransitions
	 */
	public List<PageTransition> getAllowedPageTransitions(Page page, User user) {
		List<PageTransition> allowedPageTransitions = new ArrayList<>();

		for (PageTransition pageTransition : getPageTransitions(page)) {
			if (isPageTransitionAllowed(pageTransition, user)) {
				allowedPageTransitions.add(pageTransition);
			}
		}

		return allowedPageTransitions;
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
					PageState pageState = stateService.getPageState(pageTransition.getFrom(), user);
					ExerciseState exerciseState = stateService.getExerciseState(pageState, criteria.getAffectedExercise());
					allowed = exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
				}

				if (criteria.isForPage()) {
					List<PageState> pageStates = stateService.getPageStates(user);
					allowed = pageCriteriaSatisfied(pageStates, criteria);
				}
			}
		}

		return allowed;
	}

	/**b
	 * Checks if exercise satisfies criteria
	 *
	 * @param exerciseState
	 * @param criteria
	 * @return satisfies
	 */
	private boolean exerciseCriteriaSatisfied(ExerciseState exerciseState, ExerciseCriteriaType criteria) {
		boolean satisfies = false;

		switch (criteria) {
			case noInput:
				satisfies = exerciseState.getInputState() == null || exerciseState.getInputState().isEmpty();
				break;
			case correctInput:
				satisfies = exerciseState.getInputState() != null && componentService.isExerciseCorrect(exerciseState);
				break;
			case incorrectInput:
				satisfies = exerciseState.getInputState() != null && !componentService.isExerciseCorrect(exerciseState);
				break;
		}

		return satisfies;
	}

	private boolean pageCriteriaSatisfied(List<PageState> pageStates, Criteria criteria) {
		boolean satisfied = false;

		switch (criteria.getPageCriteria()) {
			case timeExpiration:
				// TODO check how this should be checked
				break;
			case visited:
				satisfied = pageStates
						.stream()
						.anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
				break;
			case notVisited:
				satisfied = pageStates
						.stream()
						.noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
		}

		return satisfied;

	}
}
