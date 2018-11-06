package ch.uzh.marugoto.core.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotFoundException;
import ch.uzh.marugoto.core.service.ExerciseService;
import ch.uzh.marugoto.core.service.PageTransitionService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class PageTransitionServiceTest extends BaseCoreTest {
    
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private PageTransitionService pageTransitionService;
    @Autowired
    private PageTransitionRepository pageTransitionRepository;
    @Autowired
    ExerciseService exerciseService;
  
    @Test
    public void testGetPageTransittionByTransitionId() throws PageTransitionNotFoundException {
    	Page page1 = pageRepository.findByTitle("Page 1");
    	Page page2 = pageRepository.findByTitle("Page 2");
    	
    	PageTransition newPageTransition = new PageTransition(page1, page2, "Next");
    	pageTransitionRepository.save(newPageTransition);
    	
    	PageTransition pageTransition = pageTransitionService.getPageTransition(newPageTransition.getId());
    	assertThat(pageTransition.equals(newPageTransition));
    }
    
    @Test
    public void testGetPageTransittionByPageAndExercise() {
    	Page page = pageRepository.findByTitle("Page 1");
		var exercise= exerciseService.getExercises(page).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page,exercise);
    	assertThat(pageTransition.getFrom().equals(page));
    	assertThat(pageTransition.getCriteria().get(0).getAffectedExercise().getId(),is(exercise.getId()));
    }
    
    @Test
    public void testGetAllPageTransitions () {
    	Page page = pageRepository.findByTitle("Page 1");
    	List<PageTransition> pageTransitions = pageTransitionService.getAllPageTransitions(page);
    	assertFalse(pageTransitions.isEmpty());
    	assertThat (pageTransitions.size(),is(2));
    }
    
    @Test
    public void testHasPageCriteria() {
    	Page page = pageRepository.findByTitle("Page 1");
		var exercise= exerciseService.getExercises(page).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page,exercise);
    	assertTrue(pageTransitionService.hasExerciseCriteria(pageTransition));
    }
    
    @Test
    public void testHasExerciseCriteria() {
    	Page page = pageRepository.findByTitle("Page 1");
		var exercise= exerciseService.getExercises(page).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page,exercise);
    	assertTrue(pageTransitionService.hasExerciseCriteria(pageTransition));
    }
    
    
}