package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;

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

	/**
	 * Get page with the user allowed page transitions
	 * 
	 * @param id
	 * @return Page
	 */
	public Page getPage(String id) {
        return pageRepository.findById(id).orElseThrow();
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
		PageTransition pageTransition = pageTransitionRepository.findById(pageTransitionId).orElseThrow();

		if (!stateService.isPageTransitionStateAvailable(pageTransition, user)) {
			throw new PageTransitionNotAllowedException();
		}

		Page nextPage = addMoneyAndTimeToNextPage(pageTransition);
		stateService.updateStatesAfterTransition(chosenByPlayer, pageTransition, user);
		return nextPage;
	}

	private Page addMoneyAndTimeToNextPage(PageTransition pageTransition) {
		if (pageTransition.getVirtualTime() != null) {
			Duration currentTime = Duration.ofMinutes(0);
			if (pageTransition.getFrom().getVirtualTime() != null)
				currentTime = pageTransition.getFrom().getVirtualTime().getTime();

			pageTransition.getTo().setVirtualTime(new VirtualTime(currentTime.plus(pageTransition.getVirtualTime().getTime()) , true));
		}

		if (pageTransition.getMoney() != null) {
			double currentMoney = 0;
			if (pageTransition.getFrom().getMoney() != null)
				currentMoney = pageTransition.getFrom().getMoney().getAmount();

			pageTransition.getTo().setMoney(new Money(currentMoney + pageTransition.getMoney().getAmount()));
		}

		return pageTransition.getTo();
	}
}
