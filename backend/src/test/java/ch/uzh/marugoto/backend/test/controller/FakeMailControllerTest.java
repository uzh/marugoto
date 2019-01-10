package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.MailExercise;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.PageStateService;

@AutoConfigureMockMvc
public class FakeMailControllerTest extends BaseControllerTest {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	private PageState pageState6;
	private MailExercise mailExercise;
	
	public synchronized void before() {
		super.before();
        var page6 = pageRepository.findByTitle("Page 6");
        pageState6 = pageStateService.initializeStateForNewPage(page6, user);
        mailExercise = (MailExercise)componentRepository.findByPageId(pageState6.getPage().getId()).get(0);
        var exerciseState = new ExerciseState(mailExercise,"mail exercise");
        exerciseState.setPageState(pageState6);
    	exerciseStateRepository.save(exerciseState);
    }
	
	@Test
	public void testGetAllEmails() throws Exception {
		mvc.perform(authenticate(get("/api/mails/list")))
			.andExpect(status().isOk())
    		.andExpect(jsonPath("$", hasSize(1)));
	}
	
	@Test
	public void testGetEmailById() throws Exception {
		var mailExerciseId = mailExercise.getId().replaceAll("[^0-9]","");
		mvc.perform(authenticate(get("/api/mails/" + mailExerciseId)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.mailBody",is( mailExercise.getMailBody())));
	}
	
	@Test
	public void testSendEmail() throws Exception {
		var mailExerciseId = mailExercise.getId().replaceAll("[^0-9]","");
		mvc.perform(authenticate(put("/api/mails/send/" + mailExerciseId)))
			.andDo(print())
			.andExpect(status().isOk());
	}
}


