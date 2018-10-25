package ch.uzh.marugoto.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
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
	private NotebookService notebookService;

	@Autowired
	private ComponentService componentService;

	@Autowired
	private StorylineStateService storylineStateService;

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



//	/**
//	 * Returns all user states for the page
//	 * @param page
//	 * @param user
//	 * @return objectMap
//	 */
//	public HashMap<String, Object> getAllStates(Page page, User user) {
//		var objectMap = new HashMap<String, Object>();
//		PageState pageState = getPageState(page, user);
//
//		if (exerciseService.hasExercise(page))
//			objectMap.put("exerciseState", exerciseService.getAllExerciseStates(pageState));
//
//		objectMap.put("storylineState", storylineStateService.getState(pageState));
//		objectMap.put("pageState", pageState);
//
//		return objectMap;
//	}
}
