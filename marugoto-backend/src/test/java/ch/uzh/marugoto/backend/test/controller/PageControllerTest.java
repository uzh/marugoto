package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.marugoto.backend.data.entity.Chapter;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.service.PageService;
import ch.uzh.marugoto.backend.test.BaseTest;


@AutoConfigureMockMvc
public class PageControllerTest extends BaseTest{

    @Autowired
    private MockMvc mockMvc;
    

	@MockBean
	private PageService pageService;
	
	
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
		var chapter = new Chapter("Chapter 1", "icon_chapter_1");
		Page mockPage = new Page("Page 2", true, chapter, false, Duration.ofMinutes(30), true, false, false, false);
	 // Page mockPage = new Page("Page 1", true, null);
		
		FieldSetter.setField(mockPage, Page.class.getDeclaredField("id"), "1");
		Mockito.when(pageService.getPage(mockPage.getId())).thenReturn(mockPage);

		mockMvc.perform(get("/pages/"+ mockPage.getId()))
	        	.andExpect(status().isOk())
	        	.andExpect(jsonPath("$.title", is("Page 2")))
	        	.andExpect(jsonPath("$.chapter.title", is(chapter.getTitle())))
	        	.andExpect(jsonPath("$.isActive", is(true)));
		
	}
	

}
