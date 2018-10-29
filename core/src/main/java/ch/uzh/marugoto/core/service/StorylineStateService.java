package ch.uzh.marugoto.core.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.PageState;
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
    public void initializeStateForNewPage(PageState pageState, User user) {
        StorylineState storylineState = pageState.getUser().getCurrentStorylineState();

        if (pageState.getPage().isStartingStoryline()) {
            boolean newStoryline = storylineState != null && !storylineState.getStoryline().equals(pageState.getPage().getStoryline());

            if (newStoryline) {
                storylineState.setFinishedAt(LocalDateTime.now());
                storylineStateRepository.save(storylineState);
            }

            if (storylineState == null || newStoryline) {
            	storylineState = new StorylineState(pageState.getPage().getStoryline());
                storylineState.setStartedAt(LocalDateTime.now());
                updateMoneyAndTimeBalance(pageState.getPage().getMoney(), pageState.getPage().getVirtualTime(), storylineState);
                storylineStateRepository.save(storylineState);
            }

            pageState.setStorylineState(storylineState);
            pageStateRepository.save(pageState);
            user.setCurrentStorylineState(storylineState);
        }
    }

    /**
     * Updates money and timeBalance if new page is opened?
     *
     * @param pageState
     * @return storylineState
     */
    public void updateMoneyAndTimeBalance(Money money, VirtualTime time, StorylineState storylineState) {
		
		if (storylineState.getVirtualTimeBalance() != null) {
			Duration currentTime = storylineState.getVirtualTimeBalance();
			if (time != null)
				storylineState.setVirtualTimeBalance(currentTime.plus(time.getTime()));	
		}
		
		if (storylineState.getMoneyBalance() != 0) {
			double currentMoney = storylineState.getMoneyBalance();
			if (money != null)
				storylineState.setMoneyBalance(currentMoney + money.getAmount());
		}
	}
    
}
