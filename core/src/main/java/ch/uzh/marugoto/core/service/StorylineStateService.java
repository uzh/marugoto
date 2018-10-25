package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
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
     * @return
     */
    StorylineState getState(PageState pageState) {
        StorylineState storylineState = pageState.getUser().getCurrentStorylineState();

        if (pageState.getPage().isStartingStoryline()) {
            boolean newStoryline = storylineState != null && !storylineState.getStoryline().equals(pageState.getPage().getStoryline());

            if (newStoryline) {
                storylineState.setFinishedAt(LocalDateTime.now());
                storylineStateRepository.save(storylineState);
            }

            if (storylineState == null || newStoryline) {
                storylineState = createState(pageState);
            }

            pageState.setStorylineState(storylineState);
            pageStateRepository.save(pageState);
        }

        return storylineState;
    }

    /**
     * Creates story line state
     *
     * @param pageState
     * @return storylineState
     */
    private StorylineState createState(PageState pageState) {
        StorylineState storylineState = new StorylineState(pageState.getPage().getStoryline());
        storylineState.setStartedAt(LocalDateTime.now());

        if (pageState.getPage().getMoney() != null) {
            storylineState.setMoneyBalance(pageState.getPage().getMoney().getAmount());
        }
        if (pageState.getPage().getVirtualTime() != null) {
            storylineState.setVirtualTimeBalance(pageState.getPage().getVirtualTime().getTime());
        }

        storylineStateRepository.save(storylineState);
        return storylineState;
    }
}
