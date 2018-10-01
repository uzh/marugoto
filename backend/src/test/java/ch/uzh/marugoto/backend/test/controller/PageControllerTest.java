package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.backend.controller.PageController;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class PageControllerTest extends BaseControllerTest {

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	private String page1Id;
	private String page1TransitionId;
	
	@Override
	protected void setupOnce () {
		super.setupOnce();
		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		page1Id = pages.get(0).getId();
		var pageTransitions = Lists.newArrayList(pageTransitionRepository.findAll(new Sort(Direction.ASC, "_from")));
 		page1TransitionId = pageTransitions.get(0).getId();
	}
	
	@Test
	public void test1GetPage() throws Exception {
		
		mvc.perform(authenticate(get("/api/pages/" + page1Id)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageState", notNullValue()))
			.andExpect(jsonPath("$.pageState.pageTransitionStates", notNullValue()));
	}
	
	@Test
	public void test2DoPageTransition() throws Exception {
		mvc.perform(authenticate(
				post("/api/pageTransitions/doPageTransition/" + page1TransitionId)
				.param("chosenByPlayer", "true")))
				.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.storylineState", notNullValue()))
			.andExpect(jsonPath("$.pageState", notNullValue()));
	}
}