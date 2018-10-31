package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;

@Service
public class StorylineStateService {

	@Autowired
	private PageStateRepository pageStateRepository;
	@Autowired
	private StorylineStateRepository storylineStateRepository;

	/**
	 * Initialize new storylineState if a new storyline is opened, finish storyline
	 * if one is open and sets new virtual time and money
	 *
	 * @param user
	 * @return void
	 */
	public void initializeStateForNewPage(User user) {
		PageState pageState = user.getCurrentPageState();
		Page page = pageState.getPage();
		StorylineState storylineState = user.getCurrentStorylineState();

		if (page.isStartingStoryline()) {
			// finish currentStoryline
			if (storylineState != null) {
				storylineState.setFinishedAt(LocalDateTime.now());
				storylineStateRepository.save(storylineState);
			}
			// create and start new StorylineState
			storylineState = new StorylineState(page.getStoryline());
			storylineState.setStartedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);
			pageState.setStorylineState(storylineState);
			pageStateRepository.save(pageState);
			user.setCurrentStorylineState(storylineState);
		}

		// set time and money on opening page if there is a setted value
		if (page.getVirtualTime() != null) {
			storylineState.setVirtualTimeBalance(page.getVirtualTime().getTime());
		}
		if (page.getMoney() != null) {
			storylineState.setMoneyBalance(page.getMoney().getAmount());
		}
		storylineStateRepository.save(storylineState);
	}

	/**
	 * Add the money and time on transition in storyline
	 *
	 * @param pageTransition
	 * @param storylineState
	 */
	public void addMoneyAndTimeBalance(PageTransition pageTransition, StorylineState storylineState) {
		VirtualTime time = pageTransition.getVirtualTime();
		Money money = pageTransition.getMoney();

		if (storylineState.getVirtualTimeBalance() != null) {
			Duration currentTime = storylineState.getVirtualTimeBalance();
			if (time != null) {
				storylineState.setVirtualTimeBalance(currentTime.plus(time.getTime()));
			}
		}

		if (storylineState.getMoneyBalance() != 0) {
			double currentMoney = storylineState.getMoneyBalance();
			if (money != null) {
				storylineState.setMoneyBalance(currentMoney + money.getAmount());
			}
		}
	}
}
