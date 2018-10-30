package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
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
	protected PageStateRepository pageStateRepository;
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
	@Autowired
	private ModuleRepository moduleRepository;
	
	/**
	 * Update the states and returns the states
	 * @param authenticatedUser
	 * @return HashMap currentStates
	 */
	public HashMap<String, Object> getStates(User authenticatedUser) {
		PageState pageState = authenticatedUser.getCurrentPageState();
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
     * @param authenticatedUser
     * @return nextPage
     */
    public Page doPageTransition(boolean chosenByPlayer, String pageTransitionId, User authenticatedUser) throws PageTransitionNotAllowedException {
    	Page nextPage = pageTransitionStateService.doPageTransition(chosenByPlayer, pageTransitionId, authenticatedUser);
    	initializeStatesForNewPage(nextPage, authenticatedUser);
    	return nextPage;
    }
	
	/**
	 * Open first page from module
	 *
	 * @param authenticatedUser
	 * @return void
	 */
	public void openFirstPageFromModule(User authenticatedUser) {
		Page page = moduleRepository.findAll().iterator().next().getPage();
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
		
		exerciseStateService.initializeState(pageState);
		pageTransitionStateService.initializeState(pageState);
		storylineStateService.initializeState(pageState);
		notebookService.addNotebookEntry(pageState, NotebookEntryCreateAt.enter);

		user.setCurrentPageState(pageState);

		if (pageState.getStorylineState() != null) {
			user.setCurrentStorylineState(pageState.getStorylineState());
		}

		userRepository.save(user);
		
		return pageState;
	}
}