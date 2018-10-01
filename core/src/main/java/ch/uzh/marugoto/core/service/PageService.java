package ch.uzh.marugoto.core.service;

import com.arangodb.ArangoDBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

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


	/**
	 * Get page with all the belonging components
	 * 
	 * @param id
	 * @return Page
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).get();
		page.setPageTransitions(getPageTransitions(page));
		return page;
	}

	private List<PageTransition> getPageTransitions(Page page) {
		List<PageTransition> pageTransitions = new ArrayList<>();

		if (page != null) {
			pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		}

		return pageTransitions;
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
	public Page doTransition(boolean chosenByPlayer, String pageTransitionId, User user) {
		PageTransition pageTransition = pageTransitionRepository.findById(pageTransitionId).get();
		stateService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);

		Page nextPage = pageTransition.getTo();
		nextPage.setPageTransitions(getPageTransitions(nextPage));

		return nextPage;
	}

	/**
	 * Returns exercise components for the page
	 * @param page
	 * @return exercises
	 */
	public List<Exercise> getExercises(Page page) {
		List<Exercise> exercises = new ArrayList<>();

		for (Component component : page.getComponents()) {
			if (component instanceof Exercise) {
				exercises.add((Exercise) component);
			}
		}

		return exercises;
	}
}
