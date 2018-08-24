package ch.uzh.marugoto.core.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.service.PageService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Tests for PageService.
 */
public class PageServiceTest extends BaseCoreTest {

	@Autowired
	private PageService pageService;

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private ChapterRepository chapterRepository;
	
	private String page1Id;

	
	@Override
	protected void setupOnce() {
		super.setupOnce();

		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));

		var page1 = new Page("Page 1", true, null);
		var page2 = new Page("Page 2", true, chapter1, false, Duration.ofMinutes(30), true, false, false, false);
		var page3 = new Page("Page 3", false, null);

		page1Id = pageRepository.save(page1).getId();
		var page2Id = pageRepository.save(page2).getId();
		var page3Id =pageRepository.save(page3).getId();
		
		PageTransition pageTransition1 =  pageTransitionRepository.save(new PageTransition(page1, page2, "confirm"));
		PageTransition pageTransition2 =  pageTransitionRepository.save(new PageTransition(page1, page3, "submit"));
	}
	
	@Test
	public void testGetPageById() {
		var page = pageRepository.save(new Page("Test Page 2", false, null));
		var testPage = pageService.getPage(page.getId());

		assertNotNull(testPage);
		assertEquals(testPage.getId(), page.getId());
	}
	
	@Test
	public void testGetPageTransitionsByPageId () {
		List<PageTransition> pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(page1Id);
		assertNotNull(pageTransitions);
        assertThat(pageTransitions.size(), is(2));
        assertEquals(pageTransitions.get(1).getFrom().getId(), page1Id);
        assertEquals(pageTransitions.get(1).getButtonText(), "submit");

	}

}
