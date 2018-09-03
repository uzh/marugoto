package ch.uzh.marugoto.core.test.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.Duration;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple test cases for PageTransitionRepository.
 */
public class PageTransitionRepositoryTest extends BaseCoreTest{

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private ChapterRepository chapterRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Test
	public void testCreatePageTransition() {
		var chapter = chapterRepository.save(new Chapter("ChapterTransition 1", "icon_chapter_1"));
		var page1 = new Page("PageTransition1", true, null,null);
		var page2 = new Page("PageTransition2", true, chapter, null, false, Duration.ofMinutes(30), true, false, false, false);

		pageRepository.save(page1);
		pageRepository.save(page2);
		PageTransition pageTransition = pageTransitionRepository.save(new PageTransition(page1, page2, "confirm"));
		assertNotNull(pageTransition);
		assertEquals("confirm", pageTransition.getButtonText());
	}
	
	@Test
	public void testGetPageTransitionsByPageId () {
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		var page1Id = pages.get(0).getId();
		List<PageTransition> pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(page1Id);
		
		assertNotNull(pageTransitions);
        assertThat(pageTransitions.size(), is(2));
        assertEquals(pageTransitions.get(1).getFrom().getId(), page1Id);
        assertEquals(pageTransitions.get(1).getButtonText(), "submit");
	}
}
