package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Duration;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.backend.data.entity.Chapter;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageTransition;
import ch.uzh.marugoto.backend.data.repository.ChapterRepository;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Simple test cases for Page-related entities.
 * 
 * Pages & Chapters:
 *		Page 1
 * 		Page 2 --> Chapter 1
 * 		Page 3 --> Chapter 2
 * 		Page 4 --> Chapter 2
 * 		Page 5 --> Chapter 2
 * 
 * Transitions:
 *		Page 1 --> Page 2
 *             --> Page 3
 *		Page 2 --> Page 4
 *		Page 3 --> Page 4
 *		Page 4 --> Page 5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageEntitiesTest extends BaseTest {

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	
	
	@Test
	public void test1CreateChapters() throws Exception {
		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		var chapter2 = chapterRepository.save(new Chapter("Chapter 2", "icon_chapter_2"));
		
		assertNotNull(chapter1);
		assertNotNull(chapter2);
	}

	@Test
	public void test2CreatePages() throws Exception {
		// Page 1 (no chapter)
		// Page 2 -> Chapter 1
		// Page 3 -> Chapter 2
		// Page 4 -> Chapter 2
		// Page 5 -> Chapter 2

		var chapters = Lists.newArrayList(chapterRepository.findAll());

		var page1 = pageRepository.save(new Page("Page 1", true, null));
		var page2 = pageRepository.save(new Page("Page 2", true, chapters.get(0), false, Duration.ofMinutes(30), true, false, false, false));
		var page3 = pageRepository.save(new Page("Page 3", true, chapters.get(1)));
		var page4 = pageRepository.save(new Page("Page 4", true, chapters.get(1)));
		var page5 = pageRepository.save(new Page("Page 5", true, chapters.get(1)));

		assertNotNull(page1);
		assertNull(page1.getChapter());
		assertNotNull(page2);
		assertNotNull(page2.getChapter());
		assertEquals(Duration.ofMinutes(30), page2.getTimeLimit());
		assertNotNull(page3);
		assertNotNull(page3.getChapter());
		assertNotNull(page4);
		assertNotNull(page4.getChapter());
		assertNotNull(page5);
		assertNotNull(page5.getChapter());
	}
	
	@Test
	public void test3CreateTransitions() throws Exception {
		// Page 1 --> Page 2
		//        --> Page 3
		// Page 2 --> Page 4
		// Page 3 --> Page 4
		// Page 4 --> Page 5
		
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));

		var transition1to2 = pageTransitionRepository.save(new PageTransition(pages.get(0), pages.get(1), null));
		var transition1to3 = pageTransitionRepository.save(new PageTransition(pages.get(0), pages.get(2), null));
		var transition2to4 = pageTransitionRepository.save(new PageTransition(pages.get(1), pages.get(3), null));
		var transition3to4 = pageTransitionRepository.save(new PageTransition(pages.get(2), pages.get(3), null));
		var transition4to5 = pageTransitionRepository.save(new PageTransition(pages.get(3), pages.get(4), null));
		
		assertNotNull(transition1to2);
		assertNotNull(transition1to3);
		assertNotNull(transition2to4);
		assertNotNull(transition3to4);
		assertNotNull(transition4to5);
	}
}
