package ch.uzh.marugoto.core.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.NotebookContentCreateAt;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.GameStateNotInitializedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;

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
	private GameMailService mailService;

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
		states.put("gameState", pageState.getGameState());
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
    		PageState pageStateLastPage = user.getCurrentPageState();
    		GameState gameState = user.getCurrentGameState();
			PageTransition pageTransition = pageTransitionStateService.updateOnTransition(chosenBy, pageTransitionId, user);
			pageStateService.setLeftAt(pageStateLastPage);
			notebookService.addNotebookContentForPage(user, NotebookContentCreateAt.pageExit);
			Page nextPage = pageTransition.getTo();
			gameStateService.updateVirtualTimeAndMoney(pageTransition, gameState);
			initializeStatesForNewPage(nextPage, user, gameState);
    		return nextPage;
		} catch (PageTransitionNotFoundException e) {
    		throw new PageTransitionNotAllowedException(e.getMessage());
		}
    }

	/**
	 * Called when user starts a new topic
	 *
	 * @param topic topic to start
	 * @param user current user
	 */
	public void startTopic(Topic topic, User user) {
		GameState gameState = gameStateService.initializeState(user, topic);
		initializeStatesForNewPage(gameState.getTopic().getStartPage(), user, gameState);
	}
	
	/**
	 * Create all states needed for current page
	 *
	 * @param page current page
	 * @param user current user
	 */
	private void initializeStatesForNewPage(Page page, User user, GameState gameState) {
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		pageTransitionStateService.initializeStateForNewPage(user);
		notebookService.initializeStateForNewPage(user);
		gameStateService.initializeVirtualTimeAndMoney(page, gameState);

		if (page.isEndOfTopic()) {
			gameStateService.finish(user.getCurrentGameState());
		}
	}
}