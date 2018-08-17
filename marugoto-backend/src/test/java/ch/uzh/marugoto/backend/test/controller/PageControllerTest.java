package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    

	@MockBean
	private PageService pageService;
	
	@MockBean
	private PageTransition pageTransition;
	
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
	
	@Before
	public void setUp() {
		

		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));

		var page1 = new Page("Page 1", true, null);
		var page2 = new Page("Page 2", true, chapter1, false, Duration.ofMinutes(30), true, false, false, false);

		pageRepository.save(page1);
		pageRepository.save(page2);
		pageTransitionRepository.save(new PageTransition(page1, page2, null));

	}
	
	
	@Test	
	public void getPagesTest() throws Exception {
		
		Iterable<Page>mockPages = Arrays.asList(new Page("Page 1", true, null),new Page("Page 2", true, null));

		Mockito.when(pageService.getAllPages()).thenReturn(mockPages);
		mockMvc.perform(get("/pages/list"))
	        	.andExpect(status().isOk())
	        	.andExpect(jsonPath("$[0].title", is("Page 1")))
	        	.andExpect(jsonPath("$[0].isActive", is(true)))
                .andExpect(jsonPath("$[1].title", is("Page 2")));

	}
	
	@Test	
	public void getPageTest() throws Exception {

		var pages = Lists.newArrayList(pageRepository.findAll()); 
		var pageId = pages.get(pages.size() - 1).getId().replace("page/", "");
				
		mockMvc.perform(get("/pages/"+ pageId))
	        	//.andExpect("$".hasSize(2));
	        	.andExpect(jsonPath("$[0].title", is("Page 1")));
	        	//.andExpect(jsonPath("$.isActive", is(true)));
		
	}
	

}
