package ch.uzh.marugoto.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

/**
 * PageService provides functionality related to page and pageTransition entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotebookService notebookService;

	@Autowired
	private ComponentService componentService;

	@Autowired
	private StorylineService storylineService;

	@Autowired
	private PageTransitionService pageTransitionService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private PageStateService pageStateService;

	/**
	 *
	 * @param id
	 * @return
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).orElseThrow();
		page.setComponents(componentService.getPageComponents(page));
		return page;
	}

	/**
	 * Finds or creates page state for the user
	 *
	 * @param page
	 * @param user
	 * @return pageState
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateService.getState(page, user);

		if (pageState == null) {
			pageState = pageStateService.createState(page, user);

			exerciseService.createExerciseStates(pageState);
			pageTransitionService.setPageTransitionStates(pageState);

			if (page.isStartingStoryline()) {
				StorylineState storylineState = storylineService.getStorylineState(pageState);
				user.setCurrentStorylineState(storylineState);
			}
		}

		user.setCurrentPageState(pageState);
		userRepository.save(user);

		return pageState;
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
	public Page doTransition(boolean chosenByPlayer, String pageTransitionId, User user) throws PageTransitionNotAllowedException {
		PageTransition pageTransition = pageTransitionService.getPageTransition(pageTransitionId);

		try {
			if (!pageTransitionService.isTransitionAvailable(pageTransition, user))
				throw new PageTransitionNotAllowedException();

			PageState currentPageState = pageTransitionService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
			notebookService.addNotebookEntry(currentPageState, NotebookEntryCreateAt.exit);

			PageState nextPageState = getPageState(pageTransition.getTo(), user);
			notebookService.addNotebookEntry(nextPageState, NotebookEntryCreateAt.enter);

			pageTransitionService.updateMoneyAndTimeInPageTransition(pageTransition, user.getCurrentStorylineState());

		} catch (PageStateNotFoundException e) {
			throw new PageTransitionNotAllowedException(e.getMessage());
		}

		return pageTransition.getTo();
	}

	/**
	 * Returns all user states for the page
	 * @param page
	 * @param user
	 * @return objectMap
	 */
	public HashMap<String, Object> getAllStates(Page page, User user) {
		var objectMap = new HashMap<String, Object>();
		PageState pageState = getPageState(page, user);

		if (exerciseService.hasExercise(page))
			objectMap.put("exerciseState", exerciseService.getAllExerciseStates(pageState));

		objectMap.put("storylineState", storylineService.getStorylineState(pageState));
		objectMap.put("pageState", pageState);

		return objectMap;
	}
}
