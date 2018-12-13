package ch.uzh.marugoto.core.test.service;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
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
    private Page page1;
    private Page page2;

    @Before
    public synchronized void before() {
        super.before();
        page1 = pageRepository.findByTitle("Page 1");
        page2 = pageRepository.findByTitle("Page 2");
    }
  
    @Test
    public void testGetPageTransitionByTransitionId() throws PageTransitionNotFoundException {
    	PageTransition newPageTransition = new PageTransition(page1, page2, "Next");
    	pageTransitionRepository.save(newPageTransition);
    	
    	PageTransition pageTransition = pageTransitionService.getPageTransition(newPageTransition.getId());
        assertEquals(pageTransition, newPageTransition);
    }
    
    @Test
    public void testGetPageTransitionByPageAndExercise() {
		var exercise= exerciseService.getExercises(page1).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page1, exercise);
    	assertEquals(pageTransition.getFrom(), page1);
    	assertThat(pageTransition.getCriteria().get(0).getAffectedExercise().getId(),is(exercise.getId()));
    }
    
    @Test
    public void testGetAllPageTransitions () {
    	List<PageTransition> pageTransitions = pageTransitionService.getAllPageTransitions(page1);
    	assertFalse(pageTransitions.isEmpty());
    	assertThat (pageTransitions.size(),is(2));
    }
    
    @Test
    public void testHasPageCriteria() {
		var exercise= exerciseService.getExercises(page1).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page1, exercise);
    	assertTrue(pageTransitionService.hasExerciseCriteria(pageTransition));
    }
    
    @Test
    public void testHasExerciseCriteria() {
		var exercise= exerciseService.getExercises(page1).get(0);
    	PageTransition pageTransition = pageTransitionService.getPageTransition(page1, exercise);
    	assertTrue(pageTransitionService.hasExerciseCriteria(pageTransition));
    }
    
    
}