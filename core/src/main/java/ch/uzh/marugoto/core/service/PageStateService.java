package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;

@Service
public class PageStateService {

    @Autowired
    private PageStateRepository pageStateRepository;

    public PageState initializeStateForNewPage(Page page, User user) {
        PageState pageState = new PageState(page, user);
        pageState.setEnteredAt(LocalDateTime.now());
        pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
        pageStateRepository.save(pageState);
        return pageState;
    }

    public PageState getPageState(User user) throws PageStateNotFoundException {
        PageState pageState = user.getCurrentPageState();

        if (pageState == null) {
            throw new PageStateNotFoundException();
        }

        return pageState;
    }

    public void setPageTransitionStates(PageState pageState, List<PageTransitionState> pageTransitionStates) {
        pageState.setPageTransitionStates(pageTransitionStates);
        pageStateRepository.save(pageState);
    }

    public PageState updateAfterTransition(PageState pageState) {
        pageState.setLeftAt(LocalDateTime.now());
        pageStateRepository.save(pageState);
        return pageState;
    }
}
