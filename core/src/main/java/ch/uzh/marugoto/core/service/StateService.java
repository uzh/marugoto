package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {

	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;

	@Autowired
	private PageTransitionStateRepository pageTransitionStateRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	public PageState getPageState(String pageStateId) {
		return pageStateRepository.findById(pageStateId).get();
	}
	
	/**
	 * Finds the page state for the page and user
	 * 
	 * @param page
	 * @param user
	 * @return pageState
	 */
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState == null) {
			pageState = pageStateRepository.save(new PageState(page, user));
		}

		return pageState;
	}
	
	/**
	 * Finds all exercise states for the PageState
	 * 
	 * @param pageState
	 * @return exerciseStates
	 */
	public List<ExerciseState> getExerciseStates(PageState pageState) {
		List<ExerciseState> exerciseStates = exerciseStateRepository.findByPageState(pageState.getId());
		
		if (exerciseStates.isEmpty()) {			
			// create exercise states
			for (Component component : pageState.getPage().getComponents()) {
				if (component instanceof Exercise) {
					ExerciseState exerciseState = exerciseStateRepository.save(new ExerciseState((Exercise) component));
					exerciseStates.add(exerciseState);
				}
			}
		}
		
		return exerciseStates;
	}

	/**
	 * Finds the pageTransitionStates for the page and user
	 * 
	 * @param page
	 * @param user
	 * @return pageTransitionStates
	 */
	public List<PageTransitionState> getPageTransitionStates(Page page, User user) {
		List<PageTransition> pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(page.getId());
		List<PageTransitionState> pageTransitionStates = new ArrayList<PageTransitionState>();

		for (PageTransition pageTransition : pageTransitions) {
			pageTransitionStates.add(getPageTransitionState(pageTransition, user));
		}

		return pageTransitionStates;
	}
	
	/**
	 * Updates states after user page transition is done
	 * 
	 * @param chosenByPlayer
	 * @param pageTransition
	 * @param user
	 */
	public void updateStatesAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
		PageState fromPageState = getPageState(pageTransition.getFrom(), user);
		fromPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(fromPageState);
		
		PageState toPageState = getPageState(pageTransition.getTo(), user);
		toPageState.setEnteredAt(LocalDateTime.now());
		pageStateRepository.save(toPageState);
		
		PageTransitionState pageTransitionState = getPageTransitionState(pageTransition, user);
		pageTransitionState.setChosenByPlayer(chosenByPlayer);
		pageTransitionStateRepository.save(pageTransitionState);
	}
	
	/**
	 * Find PageTransitionState by PageTransition and User
	 * 
	 * @param pageTransition
	 * @param user
	 * @return pageTransitionState
	 */
	public PageTransitionState getPageTransitionState(PageTransition pageTransition, User user) {
		PageTransitionState pageTransitionState = pageTransitionStateRepository
				.findByPageTransitionAndUser(pageTransition.getId(), user.getId());

		if (pageTransitionState == null) {
			pageTransitionState = new PageTransitionState(true, pageTransition, user);
			pageTransitionStateRepository.save(pageTransitionState);
		}
		return pageTransitionState;
	}
	
	/**
	 * Updates exercise state
	 * @param pageStateId
	 * @param exerciseStateId
	 * @param inputText
	 * @return
	 */
	public ExerciseState updadeExerciseState(String exerciseStateId, String inputText) {
		ExerciseState exerciseState = exerciseStateRepository.findById(exerciseStateId).get();
		exerciseState.setInputText(inputText);
		exerciseStateRepository.save(exerciseState);
		return exerciseState;
	}
}
