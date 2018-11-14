package ch.uzh.marugoto.core.test.repository;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Exercise;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Simple test cases for PageTransitionRepository.
 */
public class PageTransitionRepositoryTest extends BaseCoreTest{

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private ComponentRepository componentRepository;

	@Test
	public void testCreatePageTransition() {

		var page1 = pageRepository.findByTitle("Page 1");
		var page2 = pageRepository.findByTitle("Page 2");

		PageTransition pageTransition = pageTransitionRepository.save(new PageTransition(page1, page2, "updateStatesAfterTransition create test"));
		assertNotNull(pageTransition);
		assertEquals("updateStatesAfterTransition create test", pageTransition.getButtonText());
	}
	
	@Test
	public void testGetPageTransitionsByPageId () {
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		var page1Id = pages.get(0).getId();
		List<PageTransition> pageTransitions = pageTransitionRepository.findByPageId(page1Id);
		
		assertNotNull(pageTransitions);
        assertThat(pageTransitions.size(), is(2));
        assertEquals(page1Id, pageTransitions.get(1).getFrom().getId());
        assertEquals("from 1 to page 3", pageTransitions.get(1).getButtonText());
	}

	@Test
	public void testFindByPageAndExercise() {
		var page = pageRepository.findByTitle("Page 2");
		var exercise = componentRepository.findByPageId(page.getId())
				.stream()
				.filter(component -> component instanceof Exercise).findFirst()
				.orElseThrow();


		var pageTransition = pageTransitionRepository.findByPageAndExercise(page.getId(), exercise.getId());
		assertNotNull(pageTransition);
	}
}
