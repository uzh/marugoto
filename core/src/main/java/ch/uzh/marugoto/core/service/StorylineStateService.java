package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		boolean changeStoryline = storylineState != null && storylineState.getStoryline().equals(page.getStoryline()) == false;

		if (changeStoryline) {
			storylineState.setFinishedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);
		}

		if (startNew || changeStoryline) {
			// create and start new StorylineState
			storylineState = new StorylineState(page.getStoryline());
			storylineState.setStartedAt(LocalDateTime.now());
			storylineStateRepository.save(storylineState);
			user.setCurrentStorylineState(storylineState);
			userService.saveUser(user);
		}

		pageStateService.updatePageState(pageState, storylineState);
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
}
