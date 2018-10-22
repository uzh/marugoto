package ch.uzh.marugoto.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

/**
 * PageService provides functionality related to page and pageTransition entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageStateRepository pageStateRepository;

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
	 *
	 * @param pageId
	 * @param user
	 * @return
	 */
	public PageState getPageState(String pageId, User user) {
		Page page = getPage(pageId);
		return getPageState(page, user);
	}

	/**
	 * Finds or creates page state for the user
	 *
	 * @param page
	 * @param user
	 * @return pageState
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageId(page.getId(), user.getId());

		if (pageState == null) {
			pageState = createPageState(page, user);

			if (hasExercise(page))
				exerciseService.createExerciseStates(pageState);

			if (page.isStartingStoryline()) {
				StorylineState storylineState = storylineService.getStorylineState(pageState);
				user.setCurrentStorylineState(storylineState);
			}

			pageTransitionService.setPageTransitionStates(pageState);
		}

		user.setCurrentPageState(pageState);
		userRepository.save(user);

		return pageState;
	}

	private PageState createPageState(Page page, User user) {
		PageState pageState = new PageState(page, user);
		pageState.setEnteredAt(LocalDateTime.now());
		pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
		pageStateRepository.save(pageState);
		return pageState;
	}

	/**
	 * Check whether page has exercise component or not
	 *
	 * @param page Page that has to be checked
	 * @return boolean
	 */
	private boolean hasExercise(Page page) {
		List<Component> components = componentService.getPageComponents(page);
		return components.stream()
				.anyMatch(component -> component instanceof Exercise);
	}

	/**
	 * Finds all user page states
	 *
	 * @param user
	 * @return pageStates
	 */
	public List<PageState> getPageStates(User user) {
		return pageStateRepository.findUserPageStates(user.getId());
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

		if (!pageTransitionService.isTransitionAvailable(pageTransition, user))
			throw new PageTransitionNotAllowedException();

		PageState currentPageState = pageTransitionService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
		notebookService.addNotebookEntry(currentPageState, NotebookEntryCreateAt.exit);

		PageState nextPageState = getPageState(pageTransition.getTo(), user);
		notebookService.addNotebookEntry(nextPageState, NotebookEntryCreateAt.enter);

		pageTransitionService.updateMoneyAndTimeInPageTransition(pageTransition, user.getCurrentStorylineState());

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

		if (hasExercise(page))
			objectMap.put("exerciseState", exerciseService.getAllExerciseStates(pageState));

		objectMap.put("storylineState", storylineService.getStorylineState(pageState));
		objectMap.put("pageState", pageState);

		return objectMap;
	}
}
