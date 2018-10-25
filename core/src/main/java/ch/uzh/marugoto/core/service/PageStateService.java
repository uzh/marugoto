package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;

/**
 * Interacts with user page state
 */
@Service
public class PageStateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected PageStateRepository pageStateRepository;

    @Autowired
    private StorylineStateService storylineStateService;

    @Autowired
    private ExerciseStateService exerciseStateService;

    @Autowired
    private PageTransitionStateService pageTransitionStateService;

    @Autowired
    private NotebookService notebookService;


    /**
     * Finds or creates page state for the user
     *
     * @param page
     * @param user
     * @return pageState
     */
    public PageState getState(Page page, User user) {
        PageState pageState = pageStateRepository.findByPageIdAndUserId(page.getId(), user.getId());

        if (pageState == null) {
            pageState = createState(page, user);

            exerciseStateService.createExerciseStates(pageState);
            pageTransitionStateService.createStates(pageState);
            notebookService.addNotebookEntry(pageState, NotebookEntryCreateAt.enter);

            if (page.isStartingStoryline()) {
                StorylineState storylineState = storylineStateService.getState(pageState);
                user.setCurrentStorylineState(storylineState);
            }
        }

        user.setCurrentPageState(pageState);
        userRepository.save(user);

        return pageState;
    }

    /**
     * Create page state
     *
     * @param page
     * @param user
     * @return
     */
    private PageState createState(Page page, User user) {
        PageState pageState = new PageState(page, user);
        pageState.setEnteredAt(LocalDateTime.now());
        pageState.setNotebookEntries(pageStateRepository.findUserNotebookEntries(user.getId()));
        pageStateRepository.save(pageState);
        return pageState;
    }
}