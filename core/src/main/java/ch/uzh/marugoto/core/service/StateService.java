package ch.uzh.marugoto.core.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.exception.UserStatesNotInitializedException;

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
	private TopicStateService topicStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private PageTransitionStateService pageTransitionStateService;
	@Autowired
	private NotebookService notebookService;
	@Autowired
	private DialogService dialogService;
	@Autowired
	private MailService mailService;


	public ExerciseService getExerciseService() {
		return exerciseStateService.getExerciseService();
	}

	/**
	 * Update the states and returns the states
	 * @param user
	 * @return HashMap currentStates
	 */
	public HashMap<String, Object> getStates(User user) throws UserStatesNotInitializedException {

		PageState pageState = user.getCurrentPageState();

		if (user.getCurrentTopicState() == null || pageState == null) {
			throw new UserStatesNotInitializedException();
		}

		var states = new HashMap<String, Object>();
		List<Component> components = getExerciseService().getPageComponents(pageState.getPage());

		if (getExerciseService().hasExercise(pageState.getPage())) {
			exerciseStateService.addExerciseStates(components, pageState);
		}

		states.put("topicState", pageState.getTopicState());
		states.put("page", pageState.getPage());
		states.put("pageComponents", components);
		states.put("pageTransitionStates", pageState.getPageTransitionStates());
		states.put("mailNotifications", mailService.getIncomingMails(pageState));
		states.put("dialogNotifications", dialogService.getIncomingDialogs(pageState.getPage()));
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
			topicStateService.updateVirtualTimeAndMoney(pageTransition.getTime(), pageTransition.getMoney(), user.getCurrentTopicState());

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
	 * @param topic
	 * @param user
	 */
	public void startTopic(Topic topic, User user) {
		topicStateService.initializeState(user, topic);
		initializeStatesForNewPage(topicService.getTopic(topic.getId()).getStartPage(), user);
	}
	
	/**
	 * Create page state
	 *
	 * @param page
	 * @param user
	 * @return 
	 */
	private void initializeStatesForNewPage(Page page, User user) {
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		pageTransitionStateService.initializeStateForNewPage(pageState);
		notebookService.addNotebookEntry(pageState, NotebookEntryAddToPageStateAt.enter);
	}
}