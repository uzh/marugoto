package ch.uzh.marugoto.core.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

/**
 * Interacts with user page state
 */
@Service
public class StateService {

	@Autowired
	private PageService pageService;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private StorylineStateService storylineStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private ExerciseService exerciseService;
	@Autowired
	private PageTransitionStateService pageTransitionStateService;
	@Autowired
	private NotebookService notebookService;
	
	/**
	 * Update the states and returns the states
	 * @param user
	 * @return HashMap currentStates
	 */
	public HashMap<String, Object> getStates(User user) {
		PageState pageState = user.getCurrentPageState();
		var states = new HashMap<String, Object>();
		states.put("pageTransitionStates", pageState.getPageTransitionStates());
		if (exerciseService.hasExercise(pageState.getPage())) {
			states.put("exerciseStates", exerciseStateService.getAllExerciseStates(pageState));
		}
		if (pageState.getStorylineState() != null) {
			states.put("storylineState", pageState.getStorylineState());
		}
		return states;
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
    public Page doPageTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
    	PageTransition pageTransition = pageTransitionStateService.updateOnTransition(chosenByPlayer, pageTransitionId, user);
		pageStateService.setLeftAt(user.getCurrentPageState());
		storylineStateService.addMoneyAndTimeBalance(pageTransition, user.getCurrentStorylineState());
		notebookService.addNotebookEntry(user.getCurrentPageState(), NotebookEntryCreateAt.exit);

		Page nextPage = pageTransition.getTo();
    	initializeStatesForNewPage(nextPage, user);
    	return nextPage;
    }

	/**
	 * Called when user visit application for the first time
	 *
	 * @param authenticatedUser
	 * @return void
	 */
	public void startModule(User authenticatedUser) {
		Page page = pageService.getModuleStartPage();
        initializeStatesForNewPage(page, authenticatedUser);
	}
	
	/**
	 * Create page state
	 *
	 * @param page
	 * @param user
	 * @return 
	 */
	private PageState initializeStatesForNewPage(Page page, User user) {
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		pageTransitionStateService.initializeStateForNewPage(pageState);
		storylineStateService.initializeStateForNewPage(user);
		notebookService.addNotebookEntry(pageState, NotebookEntryCreateAt.enter);
		return pageState;
	}
}