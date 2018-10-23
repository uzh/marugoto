package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

@Service
public class PageTransitionService {

    @Autowired
    private PageTransitionRepository pageTransitionRepository;

    @Autowired
    private PageStateRepository pageStateRepository;

    @Autowired
    private ExerciseService exerciseService;


    public PageTransition getPageTransition(String pageTransitionId) {
        return pageTransitionRepository.findById(pageTransitionId).orElseThrow();
    }

    public List<PageTransition> getAllPageTransitions(String pageTransitionId) {
        return pageTransitionRepository.findByPageId(pageTransitionId);
    }

    /**
     * Checks weather transition is available or not
     *
     * @param pageTransition
     * @param user
     * @return
     */
    boolean isTransitionAvailable(PageTransition pageTransition, User user) {
        PageState pageState = pageStateRepository.findByPageId(pageTransition.getFrom().getId(), user.getId());
        PageTransitionState pageTransitionState = pageState.getPageTransitionStates()
                .stream()
                .filter(state -> state.getPageTransition().equals(pageTransition))
                .findFirst()
                .orElseThrow();

        return pageTransitionState.isAvailable();
    }

    /**
     * Update all the states after page transition is done
     *
     * @param chosenByPlayer
     * @param pageTransition
     * @param user
     */
    PageState updateStatesAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
        PageState fromPageState = pageStateRepository.findByPageId(pageTransition.getFrom().getId(), user.getId());
        fromPageState.setLeftAt(LocalDateTime.now());
        // update page transition state
        for( PageTransitionState pageTransitionState : fromPageState.getPageTransitionStates()) {
            if (pageTransitionState.getPageTransition().equals(pageTransition)) {
                var chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
                pageTransitionState.setChosenBy(chosenBy);
                break;
            }
        }

        pageStateRepository.save(fromPageState);

        return fromPageState;
    }

    void updateMoneyAndTimeInPageTransition(PageTransition pageTransition, StorylineState storylineState) {

        if (pageTransition.getVirtualTime() != null) {
            Duration currentTime = storylineState.getVirtualTimeBalance();
            storylineState.setVirtualTimeBalance(currentTime.plus(pageTransition.getVirtualTime().getTime()));
        }
        if (pageTransition.getMoney() != null) {
            double currentMoney = storylineState.getMoneyBalance();
            storylineState.setMoneyBalance(currentMoney + pageTransition.getMoney().getAmount());
        }
    }


    /**
     * Creates page transition states for the page
     *
     * @return pageTransitionStates
     */
    void setPageTransitionStates(PageState pageState) {
        List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(pageState.getPage().getId());
        List<PageTransitionState> pageTransitionStates = new ArrayList<>();

        for (PageTransition pageTransition : pageTransitions) {
            var pageTransitionState = new PageTransitionState(pageTransition);
            // TODO
//			pageTransitionState.setAvailable(isPageTransitionAllowed(pageTransition, pageState.getUser()));
            pageTransitionStates.add(pageTransitionState);
        }

        pageState.setPageTransitionStates(pageTransitionStates);
        pageStateRepository.save(pageState);
    }

    /**
     * Checks if page transition is allowed for user
     *  TODO
     * @param pageTransition
     * @param user
     * @return allowed
     */
    private boolean isPageTransitionAllowed(PageTransition pageTransition, User user) {
        boolean allowed = true;

        if (!pageTransition.getCriteria().isEmpty()) {
            for (Criteria criteria : pageTransition.getCriteria()) {
                if (criteria.isForExercise()) {
                    PageState pageState = pageStateRepository.findByPageId(criteria.getAffectedExercise().getPage().getId(), user.getId());
                    ExerciseState exerciseState = exerciseService.getExerciseState(criteria.getAffectedExercise(), pageState);
                    allowed = exerciseService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
                }

                if (criteria.isForPage()) {
                    List<PageState> pageStates = pageStateRepository.findUserPageStates(user.getId());
                    allowed = pageCriteriaSatisfied(pageStates, criteria);
                }
            }
        }

        return allowed;
    }

    public boolean pageCriteriaSatisfied(List<PageState> pageStates, Criteria criteria) {
        boolean satisfied = false;

        switch (criteria.getPageCriteria()) {
            case timeExpiration:
                // TODO check how this should be checked
                break;
            case visited:
                satisfied = pageStates
                        .stream()
                        .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
                break;
            case notVisited:
                satisfied = pageStates
                        .stream()
                        .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
        }

        return satisfied;
    }
}
