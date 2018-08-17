package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import javax.validation.constraints.Null;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cache.support.NullValue;
import org.springframework.test.web.servlet.MockMvc;

import com.arangodb.springframework.core.ArangoOperations;
import com.google.common.collect.Lists;

import ch.uzh.marugoto.backend.data.DbConfiguration;
import ch.uzh.marugoto.backend.data.entity.Chapter;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageTransition;
import ch.uzh.marugoto.backend.data.repository.ChapterRepository;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.backend.service.PageService;
import ch.uzh.marugoto.backend.test.BaseTest;



@AutoConfigureMockMvc
public class PageControllerTest extends BaseTest{

    @Autowired
    private MockMvc mockMvc;
    

    @Autowired
	private PageService pageService;
	
	
	@Autowired
	private ArangoOperations operations;

	@Autowired
	private DbConfiguration _dbConfig;
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	
	@Autowired
	private ChapterRepository chapterRepository;
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));

		var page1 = new Page("Page 1", true, null);
		var page2 = new Page("Page 2", true, chapter1, false, Duration.ofMinutes(30), true, false, false, false);

		pageRepository.save(page1);
		pageRepository.save(page2);
		pageTransitionRepository.save(new PageTransition(page1, page2, null));

	}
	
	
	@Test	
	public void getPageTest() throws Exception {

		var pages = Lists.newArrayList(pageRepository.findAll()); 
		var pageId = pages.get(pages.size() - 1).getId().replace("page/", "");
				
		mockMvc.perform(get("/pages/"+ pageId))
	        	.andExpect(status().isOk())
	        	.andExpect(jsonPath("$.page", notNullValue()))
	        	.andExpect(jsonPath("$.pageTransitions",notNullValue() ));
		
	}
	

}
