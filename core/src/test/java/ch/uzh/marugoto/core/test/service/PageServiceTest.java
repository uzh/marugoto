package ch.uzh.marugoto.core.test.service;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for PageService.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageServiceTest extends BaseCoreTest {

	@Autowired
	private PageService pageService;

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	private User user;

	@Before
	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
	}

	@Test
	public void testGetPageById() {
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		var page1Id = pages.get(0).getId();
		var testPage = pageService.getPage(page1Id);

		assertNotNull(testPage);
		assertEquals("Page 1", testPage.getTitle());
	}
	
	@Test
	public void testDoTransition() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 1");
		
		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page.getId());
		var pageTransition = pageTransitions.get(0);
		var nextPage = pageService.doTransition(true,pageTransition.getId(), user);
		
		assertNotNull(nextPage);
		assertEquals(pageTransition.getTo().getId(), nextPage.getId());
	}

	@Test(expected = PageTransitionNotAllowedException.class)
	public void testDoTransitionWhenIsNotAllowed() throws PageTransitionNotAllowedException {
		var page = pageRepository.findByTitle("Page 2");
		var transition = pageTransitionRepository.findByPageId(page.getId()).get(0);
		transition.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, (Exercise) page.getComponents().get(0)));
		pageTransitionRepository.save(transition);

		pageService.doTransition(false, transition.getId(), user);
	}

	@Test
	public void testExerciseCriteriaIsSatisfied() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = PageService.class.getDeclaredMethod("exerciseCriteriaSatisfied", ExerciseState.class, ExerciseCriteriaType.class);
		method.setAccessible(true);

		TextExercise textExercise = new TextExercise(2, 0, 10, "Can you test exercise?");
		textExercise.addTextSolution(new TextSolution("yes", TextSolutionMode.fullmatch));
		ExerciseState exerciseState = new ExerciseState(textExercise, "yes");

		// true
		var satisfied = (boolean) method.invoke(pageService, exerciseState, ExerciseCriteriaType.correctInput);
		assertTrue(satisfied);
		// false
		satisfied = (boolean) method.invoke(pageService, exerciseState, ExerciseCriteriaType.incorrectInput);
		assertFalse(satisfied);
		// true
		exerciseState.setInputState("");
		satisfied = (boolean) method.invoke(pageService, exerciseState, ExerciseCriteriaType.noInput);
		assertTrue(satisfied);
		// true
		exerciseState.setInputState(null);
		satisfied = (boolean) method.invoke(pageService, exerciseState, ExerciseCriteriaType.noInput);
		assertTrue(satisfied);
		// false
		satisfied = (boolean) method.invoke(pageService, exerciseState, ExerciseCriteriaType.correctInput);
		assertFalse(satisfied);
	}

	@Test
	public void testIsPageTransitionAllowed() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = PageService.class.getDeclaredMethod("isPageTransitionAllowed", PageTransition.class, User.class);
		method.setAccessible(true);

		var page = pageRepository.findByTitle("Page 2");
		var pageTransition = pageTransitionRepository.findByPageId(page.getId()).get(0);
		// true
		pageTransition.addCriteria(new Criteria(ExerciseCriteriaType.noInput, (Exercise) page.getComponents().get(0)));
		var allowed = (boolean) method.invoke(pageService, pageTransition, user);
		assertTrue(allowed);
		// false
		pageTransition.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, (Exercise) page.getComponents().get(0)));
		allowed = (boolean) method.invoke(pageService, pageTransition, user);
		assertFalse(allowed);
	}
}
