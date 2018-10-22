package ch.uzh.marugoto.backend.test.controller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.service.PageService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class PageControllerTest extends BaseControllerTest {

	@Autowired
	private PageService pageService;

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Test
	public void test1GetPage() throws Exception {
		var page = pageRepository.findByTitle("Page 2");
		mvc.perform(authenticate(get("/api/pages/" + page.getId())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageState", notNullValue()))
			.andExpect(jsonPath("$.pageState.pageTransitionStates", notNullValue()));
	}

    @Test
    public void test1GetPageWithNotebookEntries() throws Exception {
        var page = pageRepository.findByTitle("Page 1");
        mvc.perform(authenticate(get("/api/pages/" + page.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageState", notNullValue()))
                .andExpect(jsonPath("$.pageState.pageTransitionStates", notNullValue()));
    }

	@Test
	public void test2DoPageTransition() throws Exception {
		var page = pageRepository.findByTitle("Page 1");
		// init page state
//		pageService.getPageState(page, user);
        var transition = pageTransitionRepository.findByPageId(page.getId()).get(0);
		mvc.perform(authenticate(
				post("/api/pageTransitions/doPageTransition/" + transition.getId())
				.param("chosenByPlayer", "true")))
				.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.storylineState", notNullValue()))
			.andExpect(jsonPath("$.pageState", notNullValue()));
	}
}