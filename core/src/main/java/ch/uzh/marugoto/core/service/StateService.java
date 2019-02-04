package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Topic;
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
	private TopicService topicService;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private StorylineStateService storylineStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private PageTransitionStateService pageTransitionStateService;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private NotificationService notificationService;


	public ExerciseService getExerciseService() {
		return exerciseStateService.getExerciseService();
	}

	/**
	 * Update the states and returns the states
	 * @param user
	 * @return HashMap currentStates
	 */
	public HashMap<String, Object> getStates(User user) {
		var states = new HashMap<String, Object>();
		PageState pageState = user.getCurrentPageState();
		List<Component> components = getExerciseService().getPageComponents(pageState.getPage());

		if (getExerciseService().hasExercise(pageState.getPage())) {
			exerciseStateService.addStateToExerciseComponents(components, pageState);
		}

		if (pageState.getStorylineState() != null) {
			states.put("storylineState", pageState.getStorylineState());
		}

		states.put("pageComponents", components);
		states.put("mailNotifications", notificationService.getMailNotifications(pageState.getPage()));
		states.put("dialogNotifications", notificationService.getDialogNotifications(pageState.getPage()));
		states.put("pageTransitionStates", pageState.getPageTransitionStates());
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
			notebookService.addNotebookEntry(user.getCurrentPageState(), NotebookEntryAddToPageStateAt.exit);
			storylineStateService.updateVirtualTimeAndMoney(pageTransition.getVirtualTime(), pageTransition.getMoney(), user.getCurrentStorylineState());

			Page nextPage = pageTransition.getTo();
			initializeStatesForNewPage(nextPage, user);
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
	public void startTopic(User authenticatedUser,Topic topic) {
		Page page = topicService.getTopicStartPage(topic.getId());
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
		notebookService.addNotebookEntry(pageState, NotebookEntryAddToPageStateAt.enter);
		return pageState;
	}
}