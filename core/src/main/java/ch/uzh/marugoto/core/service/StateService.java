package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {

	@Autowired
	private PageStateRepository pageStateRepository;

	@Autowired
	private PageTransitionStateRepository pageTransitionStateRepository;

	public List<PageTransitionState> getPageTransitionStates(List<PageTransition> pageTransitions, User user) {
		List<PageTransitionState> pageTransitionStates = new ArrayList<PageTransitionState>();

		for (PageTransition pageTransition : pageTransitions) {
			pageTransitionStates.add(this.getPageTransitionState(pageTransition, user));
		}

		return pageTransitionStates;
	}

	/**
	 * Creates page state for the page and user also initialize exercise states
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	private PageState createPageState(Page page, User user) {
		PageState pageState = new PageState(page, user);
		// add exercise states to page state
		while (page.getComponents().iterator().hasNext()) {
			var component = page.getComponents().iterator().next();
			if (component instanceof Exercise) {
				pageState.addExerciseState(this.createExerciseState((Exercise) component));
			}
		}
		pageStateRepository.save(pageState);
		return pageState;
	}

	/**
	 * Finds the page state for the page and user
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageState getPageState(Page page, User user) {
		Optional<PageState> pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState.isPresent())
			return pageState.get();

		return this.createPageState(page, user);
	}

	/**
	 * Updates current = previous PageState after user page transition is done
	 * 
	 * @param chosenByPlayer
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	public PageState updatePageStateAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
		PageState fromPageState = pageStateRepository.findByPageAndUser(pageTransition.getFrom().getId(), user.getId())
				.get();
		fromPageState.setLeftAt(LocalDateTime.now());
		pageStateRepository.save(fromPageState);
		return fromPageState;
	}

	/**
	 * Find PageTransitionState by PageTransition and User
	 * 
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	public PageTransitionState getPageTransitionState(PageTransition pageTransition, User user) {
		Optional<PageTransitionState> pageTransitionState = pageTransitionStateRepository
				.findByPageTransitionAndUser(pageTransition.getId(), user.getId());

		if (pageTransitionState.isPresent())
			return pageTransitionState.get();

		return this.createPageTransitionState(true, pageTransition, user);
	}

	/**
	 * Creates PageTransitionState
	 * 
	 * @param page
	 * @param user
	 * @return
	 */
	public PageTransitionState createPageTransitionState(boolean active, PageTransition pageTransition, User user) {
		PageTransitionState pageTransitionState = new PageTransitionState(active, pageTransition, user);
		pageTransitionStateRepository.save(pageTransitionState);
		return pageTransitionState;
	}

	public PageTransitionState updatePageTransitionState(boolean chosenByPlayer, PageTransition pageTransititon,
			User user) {
		PageTransitionState pageTransitionState = this.getPageTransitionState(pageTransititon, user);
		pageTransitionState.setChosenByPlayer(chosenByPlayer);
		pageTransitionStateRepository.save(pageTransitionState);
		return pageTransitionState;
	}

	/**
	 * Creates exercise state
	 * 
	 * @param exercise
	 * @return
	 */
	public ExerciseState createExerciseState(Exercise exercise) {
		ExerciseState exerciseState = new ExerciseState(exercise);
		return exerciseState;
	}
}
