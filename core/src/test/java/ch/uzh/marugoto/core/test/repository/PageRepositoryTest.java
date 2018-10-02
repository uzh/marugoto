package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Duration;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

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
public class PageRepositoryTest extends BaseCoreTest {

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private StorylineRepository storylineRepository;
	
	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Test
	public void testCreatePages() throws Exception {
		// Page 1 (no chapter)
		// Page 2 -> Chapter 1
		// Page 3 -> Chapter 2
		// Page 4 -> Chapter 2
		// Page 5 -> Chapter 2

		var chapters = Iterables.toArray(chapterRepository.findAll(), Chapter.class);
		var testStoryline1 = storylineRepository.save(new Storyline("Storyline_2","icon_storyline_1",Duration.ofMinutes(10),true)); 


		var page1 = pageRepository.save(new Page("Page 11", true, chapters[0]));
		var page2 = pageRepository.save(new Page("Page 12", true, chapters[0], testStoryline1, false, Duration.ofMinutes(30), true, false, false, false));
		var page3 = pageRepository.save(new Page("Page 13", true, chapters[1], testStoryline1, false, Duration.ofMinutes(5), false, false, false, false));
		var page4 = pageRepository.save(new Page("Page 14", true, chapters[1],  testStoryline1, true));
		var page5 = pageRepository.save(new Page("Page 15", true, chapters[1]));

		assertNotNull(page1);
		assertNotNull(page1.getChapter());
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
	public void testCreateTransitions() {
		var page1 = pageRepository.findByTitle("Page 1");
		var page2 = pageRepository.findByTitle("Page 2");
		var page3 = pageRepository.findByTitle("Page 3");
		var page4 = pageRepository.findByTitle("Page 4");

		// Page 1 --> Page 2
		//        --> Page 3
		// Page 2 --> Page 4
		// Page 3 --> Page 4
		
		var transition1to2 = pageTransitionRepository.save(new PageTransition(page1, page2, "1 -> 2"));
		var transition1to3 = pageTransitionRepository.save(new PageTransition(page1, page3, "1 -> 3"));
		var transition2to4 = pageTransitionRepository.save(new PageTransition(page2, page4, "2 -> 4"));
		var transition3to4 = pageTransitionRepository.save(new PageTransition(page3, page4, "3 -> 4"));

		assertNotNull(transition1to2);
		assertNotNull(transition1to3);
		assertNotNull(transition2to4);
		assertNotNull(transition3to4);
	}
}
