package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

@AutoConfigureMockMvc
public class PageControllerTest extends BaseControllerTest {

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

		page1Id = pageRepository.save(page1).getId();
		pageRepository.save(page2).getId();
		
		pageTransitionRepository.save(new PageTransition(page1, page2, null));
	}

//	@Test
	public void getPageTest() throws Exception {
		mvc.perform(authenticate(get("/api/pages/" + page1Id)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.page", notNullValue()))
			.andExpect(jsonPath("$.pageState", notNullValue()))
			.andExpect(jsonPath("$.pageTransitions", notNullValue()));
	}
}
