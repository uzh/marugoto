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
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {
	
	@Autowired
	private ComponentService componentService;
	
	@Test
	public void test1CheckExerciseSolution() {
		var exercise1 = new TextExercise(100, 100, 400, 400, 5, 25, "Wording", "What does 'domo arigato' mean?", 20);
		exercise1.addTextSolution(new TextSolution("Thank you"));
		exercise1.addTextSolution(new TextSolution("Thank's"));
		componentService.getRepository().save(exercise1);
		

		boolean solved1 = componentService.checkExerciseSolution(exercise1, "Thank you");
		boolean solved2 = componentService.checkExerciseSolution(exercise1, "Fuck you");

		assertTrue(solved1);
		assertFalse(solved2);
	}
}
