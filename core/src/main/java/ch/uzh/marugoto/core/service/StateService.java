package ch.uzh.marugoto.core.service;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

/**
 * Interacts with user page state
 */
@Service
public class StateService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PageStateRepository pageStateRepository;
	@Autowired
	private PageService pageService;
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
    	PageTransition pageTransition = pageTransitionStateService.updateAfterTransition(chosenByPlayer, pageTransitionId, user);
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
		PageState pageState = new PageState(page, user);
		pageState.setEnteredAt(LocalDateTime.now());
		pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
		pageStateRepository.save(pageState);
		user.setCurrentPageState(pageState);
		
		exerciseStateService.initializeStateForNewPage(pageState);
		pageTransitionStateService.initializeStateForNewPage(pageState);
		storylineStateService.initializeStateForNewPage(user);
		notebookService.addNotebookEntry(pageState, NotebookEntryCreateAt.enter);
		
		userRepository.save(user);
		return pageState;
	}
}