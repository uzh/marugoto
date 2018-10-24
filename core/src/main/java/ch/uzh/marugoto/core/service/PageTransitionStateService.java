package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

@Service
public class PageTransitionStateService {

    @Autowired
    private PageStateRepository pageStateRepository;

    /**
     * Return if page state is available for transition
     *
     * @param pageState
     * @param pageTransition
     * @return
     */
    boolean isStateAvailable(PageState pageState, PageTransition pageTransition) {
        return pageState.getPageTransitionStates()
                .stream()
                .filter(state -> state.getPageTransition().equals(pageTransition))
                .findFirst()
                .orElseThrow()
                .isAvailable();
    }

    /**
     * Updates isAvailable property
     *
     * @param pageState
     * @param pageTransition
     * @param available
     * @return pageState
     */
    PageState updateState(PageState pageState, PageTransition pageTransition, boolean available) {
        for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
            if (pageTransitionState.getPageTransition().equals(pageTransition)) {
                pageTransitionState.setAvailable(available);
                break;
            }
        }

        pageStateRepository.save(pageState);
        return pageState;
    }

    /**
     * Updates chosenBy property
     *
     * @param pageState
     * @param pageTransition
     * @param chosenBy
     * @return pageState
     */
    PageState updateState(PageState pageState, PageTransition pageTransition, TransitionChosenOptions chosenBy) {
        for( PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
            if (pageTransitionState.getPageTransition().equals(pageTransition)) {
                pageTransitionState.setChosenBy(chosenBy);
                break;
            }
        }

        pageStateRepository.save(pageState);
        return pageState;
    }

    /**
     * Creates states for page transitions
     *
     * @param pageState
     * @param allPageTransitions
     */
    PageState createStates(PageState pageState, List<PageTransition> allPageTransitions) {
        List<PageTransitionState> pageTransitionStates = new ArrayList<>();

        for (PageTransition pageTransition : allPageTransitions) {
            var pageTransitionState = new PageTransitionState(pageTransition);
            pageTransitionStates.add(pageTransitionState);
        }

        pageState.setPageTransitionStates(pageTransitionStates);
        pageStateRepository.save(pageState);
        return pageState;
    }
}
