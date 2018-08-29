package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
		return page;
	}
	
	/**
	 * Get all page transitions
	 * 
	 * @param id
	 * @return List<PageTransition>
	 */
	public List<PageTransition> getPageTransitions(String id) {
		List<PageTransition> pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(id);
		return pageTransitions;
	}
	
	/**
	 * Get page transition by ID
	 * 
	 * @param pageTransitionId
	 * @return
	 */
	public PageTransition getPageTransition(String pageTransitionId) {
		PageTransition pageTransition = pageTransitionRepository.findById(pageTransitionId).get();
		return pageTransition;
	}
	
	/**
	 * Transition: from page - to page
	 * Updates previous page states and returns next page
	 * 
	 * @param pageTransition
	 * @return Page
	 */
	public Page doTransition(boolean chosenByPlayer, String pageTransitionId, User user) {
		PageTransition pageTransition = this.getPageTransition(pageTransitionId);
		stateService.updatePageStateAfterTransition(chosenByPlayer, pageTransition, user);
		stateService.updatePageTransitionState(chosenByPlayer, pageTransition, user);

		return pageTransition.getTo();
	}
	
	public Page checkExercise(String pageId, User user) {
		return null;
	}
}
