package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class PageStateService {

    @Autowired
    private PageStateRepository pageStateRepository;
    @Autowired
    private UserService userService;

    public PageState initializeStateForNewPage(Page page, User user) {
        PageState pageState = new PageState(page, user);
        pageState.setEnteredAt(LocalDateTime.now());
        pageStateRepository.save(pageState);

        user.setCurrentPageState(pageState);
        userService.saveUser(user);
        return pageState;
    }

    public List<PageState> getPageStates(User user) {
        return pageStateRepository.findUserPageStates(user.getId());
    }
    
    public void setLeftAt(PageState pageState) {
        pageState.setLeftAt(LocalDateTime.now());
        pageStateRepository.save(pageState);
    }

    public void savePageState(PageState pageState) {
    	pageStateRepository.save(pageState);
    }
}
