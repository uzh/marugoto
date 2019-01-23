package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class PageStateService {

    @Autowired
    private UserService userService;
    @Autowired
    private PageStateRepository pageStateRepository;

    public PageState initializeStateForNewPage(Page page, User user) {
        PageState pageState = new PageState(page, user);
        pageState.setEnteredAt(LocalDateTime.now());
        savePageState(pageState);
        user.setCurrentPageState(pageState);
        userService.saveUser(user);
        return pageState;
    }

    public List<PageState> getPageStates(User user) {
        return pageStateRepository.findUserPageStates(user.getId());
    }
    
    public void setLeftAt(PageState pageState) {
        pageState.setLeftAt(LocalDateTime.now());
        savePageState(pageState);
    }

    public void updatePageState(PageState pageState, StorylineState storylineState) {
        pageState.setStorylineState(storylineState);
        savePageState(pageState);
    }

    /**
     * Update page state with page transition state list
     *
     * @param pageState
     * @param pageTransitionStates
     */
    public void updatePageState(PageState pageState, List<PageTransitionState> pageTransitionStates) {
        pageState.setPageTransitionStates(pageTransitionStates);
        savePageState(pageState);
    }

    public void savePageState(PageState pageState) {
    	pageStateRepository.save(pageState);
    }
}
