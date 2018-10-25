package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

@Service
public class PageTransitionService {

    @Autowired
    private PageTransitionRepository pageTransitionRepository;

    @Autowired
    private ExerciseService exerciseService;



    public PageTransition getPageTransition(String pageTransitionId) {
        return pageTransitionRepository.findById(pageTransitionId).orElseThrow();
    }

    public PageTransition getPageTransition(Page page, Exercise exercise) {
        return pageTransitionRepository.findByPageAndExercise(page.getId(), exercise.getId());
    }

    public List<PageTransition> getAllPageTransitions(Page page) {
        return pageTransitionRepository.findByPageId(page.getId());
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
     * Checks criteria that depends on the exercise
     *
     * @param pageTransition
     * @param exerciseState
     * @return
     */
    boolean isCriteriaSatisfied(PageTransition pageTransition, ExerciseState exerciseState) {
        boolean satisfied = false;

        for (Criteria criteria : pageTransition.getCriteria()) {
            if (criteria.isForExercise()) {
                satisfied = exerciseService.exerciseCriteriaSatisfied(exerciseState, criteria.getExerciseCriteria());
            }
        }

        return satisfied;
    }

//    /**
//     * Checks criteria that depends on the page
//     *
//     * @param pageTransition
//     * @param pageStateList
//     * @return
//     */
//    private boolean isCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
//        boolean satisfied = false;
//
//        for (Criteria criteria : pageTransition.getCriteria()) {
//            switch (criteria.getPageCriteria()) {
//                case timeExpiration:
//                    // TODO check how this should be checked
//                    break;
//                case visited:
//                    satisfied = pageStateList
//                            .stream()
//                            .anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
//                    break;
//                case notVisited:
//                    satisfied = pageStateList
//                            .stream()
//                            .noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
//            }
//        }
//
//        return satisfied;
//    }
}
