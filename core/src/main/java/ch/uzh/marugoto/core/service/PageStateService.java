package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class PageStateService {

    @Autowired
    private UserService userService;
    @Autowired
    private PageStateRepository pageStateRepository;

    public PageState initializeStateForNewPage(Page page, User user) {
        PageState pageState = savePageState(new PageState(page, user, user.getCurrentTopicState()));
        userService.updatePageState(user, pageState);
        return pageState;
    }

    public List<PageState> getPageStates(User user) {
        return pageStateRepository.findUserPageStates(user.getId());
    }
    
    public void setLeftAt(PageState pageState) {
        pageState.setLeftAt(LocalDateTime.now());
        savePageState(pageState);
    }

    /**
     * Update page state with page transition state list
     *
     * @param pageState
     * @param pageTransitionStates
     */
    public void updatePageTransitionStates(PageState pageState, List<PageTransitionState> pageTransitionStates) {
        pageState.setPageTransitionStates(pageTransitionStates);
        savePageState(pageState);
    }

    public PageState savePageState(PageState pageState) {
    	return pageStateRepository.save(pageState);
    }
}
