package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * PageService provides functionality related to page and pageTransition entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Autowired
	private StateService stateService;
	
	@Autowired
	private ComponentService componentService;


	/**
	 * Get page with all the belonging components
	 * 
	 * @param id
	 * @return Page
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).get();
		return page;
	}
	
	/**
	 * Transition: from page - to page
	 * Updates previous page states and returns next page
	 * 
	 * @param pageTransition
	 * @return nextPage
	 */
	public Page doTransition(boolean chosenByPlayer, String pageTransitionId, User user) {
		PageTransition pageTransition = pageTransitionRepository.findById(pageTransitionId).get();
		stateService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
		return pageTransition.getTo();
	}
	
	public boolean checkTextExercise(ExerciseState exerciseState, User user) {
		TextExercise textExercise = (TextExercise) exerciseState.getExercise();
		boolean exerciseSolved = componentService.checkTextExercise(textExercise.getTextSolutions(), exerciseState.getInputText());
		return exerciseSolved;
	}
}
