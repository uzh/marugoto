package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.Storyline;
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
     * Find/Create new storylineState for the user and
     * finishes current storylineState if exists
     *
     * @param pageState
     * @return void
     */
    public void initializeState(PageState pageState) {
        Page page = pageState.getPage();

        if (page.isStartingStoryline()) {
            StorylineState storylineState = new StorylineState(page.getStoryline());
            storylineState.setStartedAt(LocalDateTime.now());
            storylineStateRepository.save(storylineState);
            updateMoneyAndTimeBalance(page.getMoney(), page.getVirtualTime(), pageState.getUser());

            pageState.setStorylineState(storylineState);
            pageStateRepository.save(pageState);
        }
    }

    public void finishStoryline(User user) {
        StorylineState storylineState = user.getCurrentStorylineState();
        storylineState.setFinishedAt(LocalDateTime.now());
        storylineStateRepository.save(storylineState);
    }

    /**
     * Updates money and time balance in storyline
     *
     * @param money
     * @param time
     * @param user
     */
    public void updateMoneyAndTimeBalance(Money money, VirtualTime time, User user) {
        StorylineState storylineState = user.getCurrentStorylineState();

        if (storylineState != null) {
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

            storylineStateRepository.save(storylineState);
        }
	}
    
}
