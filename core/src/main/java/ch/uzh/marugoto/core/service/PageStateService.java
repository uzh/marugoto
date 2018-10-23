package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class PageStateService {

    @Autowired
    private PageStateRepository pageStateRepository;

    PageState getState(Page page, User user) {
        return pageStateRepository.findByPageIdAndUserId(page.getId(), user.getId());
    }

    PageState createState(Page page, User user) {
        PageState pageState = new PageState(page, user);
        pageState.setEnteredAt(LocalDateTime.now());
        pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
        pageStateRepository.save(pageState);
        return pageState;
    }
}
