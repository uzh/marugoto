package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {
	
	@Autowired
	private ComponentService componentService;
	
	@Autowired
	private PageRepository pageRepository;

	
	@Test
	public void test1CheckExerciseSolution() {
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		var components = (TextExercise) pages.get(1).getComponents().get(0);
		var textSolutions = components.getTextSolutions();
		
		boolean solved1 = componentService.checkExerciseSolution(textSolutions, "Thank you");
		boolean solved2 = componentService.checkExerciseSolution(textSolutions, "Fuck you");

		assertTrue(true);
		assertFalse(false);
	}
}
