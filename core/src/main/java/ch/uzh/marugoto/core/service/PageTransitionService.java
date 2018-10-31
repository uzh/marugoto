package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

@Service
public class PageTransitionService {
	
	@Autowired
    private PageTransitionRepository pageTransitionRepository;

    public PageTransition getPageTransition(String pageTransitionId) {
        return pageTransitionRepository.findById(pageTransitionId).orElseThrow();
    }
    public PageTransition getPageTransition(Page page, Exercise exercise) {
        return pageTransitionRepository.findByPageAndExercise(page.getId(), exercise.getId()).orElseThrow();
    }

    public List<PageTransition> getAllPageTransitions(Page page) {
        return pageTransitionRepository.findByPageId(page.getId());
    }
}
