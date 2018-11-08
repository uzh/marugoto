package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;

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
		if (!pageState.getNotebookEntries().isEmpty()) {
			states.put("notebookEntries", pageState.getNotebookEntries());
		}
		return states;
	}
	
	 /**
     * Transition: from page - to page
     * Updates previous page states and returns next page
     *
     * @param chosenBy
     * @param pageTransitionId
     * @param user
     * @return nextPage
     */
    public Page doPageTransition(TransitionChosenOptions chosenBy, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
    	try {
			PageTransition pageTransition = pageTransitionStateService.updateOnTransition(chosenBy, pageTransitionId, user);
			pageStateService.setLeftAt(user.getCurrentPageState());
			notebookService.addNotebookEntry(user.getCurrentPageState(), NotebookEntryCreateAt.exit);

			Page nextPage = pageTransition.getTo();
			initializeStatesForNewPage(nextPage, user);
			storylineStateService.updateVirtualTimeAndMoney(pageTransition.getVirtualTime(), pageTransition.getMoney(), user.getCurrentStorylineState());
    		return nextPage;
		} catch (PageTransitionNotFoundException e) {
    		throw new PageTransitionNotAllowedException(e.getMessage());
		}
    }

	/**
	 * Called when user visit application for the first time
	 *
	 * @param authenticatedUser
	 * @return void
	 */
	public void startModule(User authenticatedUser) {
		Page page = pageService.getTopicStartPage();
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