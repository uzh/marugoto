package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;

@Service
public class PageTransitionService {
	
	@Autowired
    private PageTransitionRepository pageTransitionRepository;

    public PageTransition getPageTransition(String pageTransitionId) throws PageTransitionNotFoundException {
        return pageTransitionRepository.findById(pageTransitionId).orElseThrow(PageTransitionNotFoundException::new);
    }

    public PageTransition getPageTransition(Page page, Exercise exercise) {
        return pageTransitionRepository.findByPageAndExercise(page.getId(), exercise.getId()).orElseThrow();
    }

    public List<PageTransition> getAllPageTransitions(Page page) {
        return pageTransitionRepository.findByPageId(page.getId());
    }

    public boolean hasPageCriteria(PageTransition pageTransition) {
        return pageTransition.getCriteria().stream().anyMatch(Criteria::isForPage);
    }

    public boolean hasExerciseCriteria(PageTransition pageTransition) {
        return pageTransition.getCriteria().stream().anyMatch(Criteria::isForExercise);
    }
}
