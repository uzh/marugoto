package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;

@Service
public class PageTransitionService {

    @Autowired
    private PageTransitionRepository pageTransitionRepository;

    @Autowired
    private PageStateRepository pageStateRepository;

    @Autowired
    private PageTransitionStateService pageTransitionStateService;

    @Autowired
    private ExerciseService exerciseService;



    public PageTransition getPageTransition(String pageTransitionId) {
        return pageTransitionRepository.findById(pageTransitionId).orElseThrow();
    }

    public List<PageTransition> getAllPageTransitions(Page page) {
        return pageTransitionRepository.findByPageId(page.getId());
    }

    /**
     * Checks weather transition is available or not
     *
     * @param pageTransition
     * @param user
     * @return
     */
    boolean isTransitionAvailable(PageTransition pageTransition, User user) throws PageStateNotFoundException {
        PageState pageState = user.getCurrentPageState();

        if (pageState == null)
            throw new PageStateNotFoundException();

        return pageTransitionStateService.isStateAvailable(pageState, pageTransition);
    }

    /**
     * Updates page transition state according to criteria and exercise
     *
     * @param exerciseState
     */
    public void updateTransitionAvailability(ExerciseState exerciseState) {
        PageTransition pageTransition = pageTransitionRepository
                .findByPageAndExercise(exerciseState.getPageState().getPage().getId(), exerciseState.getExercise().getId());
        // Update transition state
        boolean exerciseCriteriaSatisfied = isCriteriaSatisfied(pageTransition, exerciseState);
        pageTransitionStateService.updateState(exerciseState.getPageState(), pageTransition, exerciseCriteriaSatisfied);
    }

    /**
     * Update all the states after page transition is done
     *
     * @param chosenByPlayer
     * @param pageTransition
     * @param user
     */
    PageState updateStatesAfterTransition(boolean chosenByPlayer, PageTransition pageTransition, User user) {
        PageState fromPageState = user.getCurrentPageState();
        fromPageState.setLeftAt(LocalDateTime.now());
        pageStateRepository.save(fromPageState);

        var chosenBy = chosenByPlayer ? TransitionChosenOptions.player : TransitionChosenOptions.autoTransition;
        pageTransitionStateService.updateState(fromPageState, pageTransition, chosenBy);

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
     * Create page transition states
     *
     */
    void setPageTransitionStates(PageState pageState) {
        pageTransitionStateService.createStates(pageState, getAllPageTransitions(pageState.getPage()));
    }

    /**
     * Checks criteria that depends on the exercise
     *
     * @param pageTransition
     * @param exerciseState
     * @return
     */
    private boolean isCriteriaSatisfied(PageTransition pageTransition, ExerciseState exerciseState) {
        boolean satisfied = false;

        for (Criteria criteria : pageTransition.getCriteria()) {
            if (criteria.isForExercise()) {
                satisfied = exerciseService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
            }
        }

        return satisfied;
    }

    /**
     * Checks criteria that depends on the page
     *
     * @param pageTransition
     * @param pageStateList
     * @return
     */
    private boolean isCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
        boolean satisfied = false;

        for (Criteria criteria : pageTransition.getCriteria()) {
            switch (criteria.getPageCriteria()) {
                case timeExpiration:
                    // TODO check how this should be checked
                    break;
                case visited:
                    satisfied = pageStateList
                            .stream()
                            .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
                    break;
                case notVisited:
                    satisfied = pageStateList
                            .stream()
                            .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
            }
        }

        return satisfied;
    }
}
