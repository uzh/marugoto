package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
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
	private PageStateRepository pageStateRepository;


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
	 * Creates page state for user
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageState createPageStage(Page page, User user) {
		PageState pageState = new PageState(page, user);
		pageStateRepository.save(pageState);
		return pageState;
	}
	
	/**
	 * Retrieves page state
	 * it will create new state if not exist 
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState == null)
			pageState = this.createPageStage(page, user);

		return pageState;
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
	 * Updates previous page state and returns next page
	 * 
	 * @param pageTransitionId
	 * @param user
	 * @return Page
	 */
	public Page doTransition(String pageTransitionId, User user) {
		PageTransition pageTransition = this.getPageTransition(pageTransitionId);
		// update from page state
		PageState fromPageState = pageStateRepository.findByPageAndUser(pageTransition.getFrom().getId(), user.getId());
		fromPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(fromPageState);

		return pageTransition.getTo();
	}
}
