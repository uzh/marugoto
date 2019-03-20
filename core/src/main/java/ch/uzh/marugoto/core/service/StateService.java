package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.NotebookContentCreateAt;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.exception.GameStateNotInitializedException;

/**
 * Interacts with user page state
 */
@Service
public class StateService {

	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private GameStateService gameStateService;
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

	/**
	 * Update the states and returns the states
	 *
	 * @param user current user
	 * @return HashMap currentStates
	 */
	public HashMap<String, Object> getStates(User user) throws GameStateNotInitializedException {

		PageState pageState = user.getCurrentPageState();

		if (user.getCurrentGameState() == null || pageState == null) {
			throw new GameStateNotInitializedException();
		}

		var states = new HashMap<String, Object>();
		states.put("topicState", pageState.getGameState());
		states.put("page", pageState.getPage());
		states.put("pageComponents", exerciseStateService.getComponentResources(pageState));
		states.put("pageTransitionStates", pageState.getPageTransitionStates());
		states.put("mailNotifications", mailService.getIncomingMails(user));
		states.put("dialogNotifications", dialogService.getIncomingDialogs(user));
		return states;
	}
	
	 /**
     * Transition: from page - to page
     * Updates previous page states and returns next page
     *
     * @param chosenBy none / player / autoTransition
     * @param pageTransitionId page transition ID
     * @param user current user
     * @return nextPage
     */
    public Page doPageTransition(TransitionChosenOptions chosenBy, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
    	try {
			PageTransition pageTransition = pageTransitionStateService.updateOnTransition(chosenBy, pageTransitionId, user);
			pageStateService.setLeftAt(user.getCurrentPageState());
			notebookService.addNotebookContentForPage(user, NotebookContentCreateAt.pageExit);
			gameStateService.updateVirtualTimeAndMoney(pageTransition.getTime(), pageTransition.getMoney(), user.getCurrentGameState());
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
	 * @param topic topic to start
	 * @param user current user
	 */
	public void startTopic(Topic topic, User user) {
		GameState gameState = gameStateService.initializeState(user, topic);

		if (user.getCurrentPageState() == null) {
			initializeStatesForNewPage(gameState.getTopic().getStartPage(), user);
		}
	}
	
	/**
	 * Create all states needed for current page
	 *
	 * @param page current page
	 * @param user current user
	 */
	private void initializeStatesForNewPage(Page page, User user) {
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		pageTransitionStateService.initializeStateForNewPage(user);
		notebookService.initializeStateForNewPage(user);

		if (page.isEndOfTopic()) {
			gameStateService.finish(user.getCurrentGameState());
		}
	}
}