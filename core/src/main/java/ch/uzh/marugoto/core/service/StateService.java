package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {

	@Autowired
	private PageStateRepository pageStateRepository;

	@Autowired
	private PageTransitionStateRepository pageTransitionStateRepository;

	/**
	 * Creates page state for the page and user
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageState createPageState(Page page, User user) {
		PageState pageState = new PageState(page, user);
		pageStateRepository.save(pageState);
		return pageState;
	}
	
	/**
	 * Finds the page state for the page and user
	 * if state is not present it will create new one
	 *  
	 * @param page
	 * @param user
	 * @return
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState == null)
			pageState = this.createPageState(page, user);	

		return pageState;
	}
	
	/**
	 * It updates leftAt for page state and add's page transition state
	 * 
	 * @param chosenByPlayer
	 * @param pageTransition
	 * @param user
	 */
	public void updatePageStateAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
		PageState currentPageState = pageStateRepository.findByPageAndUser(pageTransition.getFrom().getId(), user.getId());
		currentPageState.addPageTransitionState(this.createPageTransitionState(false, chosenByPlayer, pageTransition));
		currentPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(currentPageState);
	}
	
	/**
	 * Creates page state for the page and user
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageTransitionState createPageTransitionState(boolean active, boolean chosenByPlayer, PageTransition pageTransition) {
		PageTransitionState pageTransitionState = new PageTransitionState(active, chosenByPlayer, pageTransition);
		pageTransitionStateRepository.save(pageTransitionState);
		return pageTransitionState;
	}
	
	/**
	 * Finds all page transition states for the page
	 * 
	 * @param pageTransitions
	 * @param user
	 * @return
	 */
	public List<PageTransitionState> getPageTransitionStates(List<PageTransition> pageTransitions) {
		List<PageTransitionState> pageTransitionStates = new ArrayList<PageTransitionState>();

		for (var i = 0; i < pageTransitions.size(); i++) {
			PageTransitionState pageTransitionState = this.getPageTransitionState(pageTransitions.get(i));

			if (pageTransitionState != null)
				pageTransitionStates.add(pageTransitionState);
		}

		return pageTransitionStates;
	}
	
	/**
	 * Finds page transition state 
	 * 
	 * @param pageTransitionId
	 * @param user
	 * @return
	 */
	public PageTransitionState getPageTransitionState(PageTransition pageTransition) {
		PageTransitionState pageTransitionState = pageTransitionStateRepository.findByPageTransition(pageTransition.getId());
		return pageTransitionState;
	}
}
