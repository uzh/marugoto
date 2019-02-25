package ch.uzh.marugoto.backend.test.controller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

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
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	public synchronized void before() {
		super.before();
	}

	@Test
	public void testGetPage() throws Exception {

		mvc.perform(authenticate(get("/api/pages/current")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageComponents", notNullValue()))
			.andExpect(jsonPath("$.pageTransitionStates", notNullValue()));
	}

	@Test
	public void test2DoPageTransition() throws Exception {
		var page = pageRepository.findByTitle("Page 1");
        var transitions = pageTransitionRepository.findByPageId(page.getId());
        var transition = transitions.stream()
				.filter(pageTransition -> pageTransition.getCriteria().isEmpty())
				.findAny()
				.orElse(null);

		mvc.perform(authenticate(
				post("/api/pageTransitions/doPageTransition/" + transition.getId())
				.param("chosenByPlayer", "true")))
				.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.page", notNullValue()));
	}
}