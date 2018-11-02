package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;

@Service
public class StorylineStateService {

	@Autowired
	private StorylineStateRepository storylineStateRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private PageStateService pageStateService;

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
		boolean startNew = storylineState == null && page.getStoryline() != null;
		boolean finishStoryline = storylineState != null && !storylineState.getStoryline().equals(page.getStoryline());

		if (finishStoryline) {
			storylineState.setFinishedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);
		}

		if (startNew) {
			// create and start new StorylineState
			storylineState = new StorylineState(page.getStoryline());
			storylineState.setStartedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);
			pageState.setStorylineState(storylineState);
			pageStateService.savePageState(pageState);
			user.setCurrentStorylineState(storylineState);
			userService.saveUser(user);
		}

		updateVirtualTimeAndMoney(page.getVirtualTime(), page.getMoney(), storylineState);
	}

	/**
	 * Update money and time in storyline
	 *
	 * @param virtualTime
	 * @param money
	 * @param storylineState
	 */
	public void updateVirtualTimeAndMoney(@Nullable VirtualTime virtualTime, Money money, @Nullable StorylineState storylineState) {
		if (storylineState != null) {
			Duration currentTime = storylineState.getVirtualTimeBalance();
			if (virtualTime != null) {
				storylineState.setVirtualTimeBalance(currentTime.plus(virtualTime.getTime()));
			}

			double currentMoney = storylineState.getMoneyBalance();
			if (money != null) {
				storylineState.setMoneyBalance(currentMoney + money.getAmount());
			}

			storylineStateRepository.save(storylineState);
		}
	}

	private boolean startNewStoryline(Page page, StorylineState storylineState) {
		return  storylineState == null && page.getStoryline() != null;
//		boolean ifExistAndPageHasDifferentStoryline = storylineState != null && !storylineState.getStoryline().equals(page.getStoryline());
//		return ifNotExistAndPageHasStoryline || ifExistAndPageHasDifferentStoryline;
	}

	private boolean changeStoryline(Page page, StorylineState storylineState) {
		return storylineState != null && !storylineState.getStoryline().equals(page.getStoryline());
	}
}
