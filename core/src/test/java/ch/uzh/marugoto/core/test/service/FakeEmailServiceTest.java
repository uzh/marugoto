package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.MailExercise;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.FakeEmailService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class FakeEmailServiceTest extends BaseCoreTest {
	
	@Autowired
	private FakeEmailService fakeEmailService;
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
    private PageStateService pageStateService;
	@Autowired 
	private ExerciseStateRepository exerciseStateRepository;
    private PageState pageState6;
    private ExerciseState exerciseState;
	
	public synchronized void before() {
		super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
        var page6 = pageRepository.findByTitle("Page 6");
        pageState6 = pageStateService.initializeStateForNewPage(page6, user);
        var mailExercise = (MailExercise)componentRepository.findByPageIdOrderByRenderOrderAsc(pageState6.getPage().getId()).get(0);
        exerciseState = new ExerciseState(mailExercise,"mail exercise");
        exerciseState.setPageState(pageState6);
    	exerciseStateRepository.save(exerciseState);
    }
	
	@Test
	public void testGetAllMailExercises() {
		var user = userRepository.findByMail("unittest@marugoto.ch");
		var mailExercises = fakeEmailService.getAllMailExercises(user.getId());
        assertThat(mailExercises.get(0), instanceOf(MailExercise.class));
		assertEquals(mailExercises.size(), 1);
	}
	
	@Test 
	public void testGetMailExerciseById() {
		var mailExercise = componentRepository.findByPageIdOrderByRenderOrderAsc(pageState6.getPage().getId()).get(0);
		var exercise = fakeEmailService.getMailExerciseById(pageState6.getId(), mailExercise.getId());
		assertThat(exercise, instanceOf(MailExercise.class));
		assertEquals(exercise.getId(), mailExercise.getId());
	}
	
	@Test
	public void testSendEmail() {
		var mailExercise = (MailExercise)componentRepository.findByPageIdOrderByRenderOrderAsc(pageState6.getPage().getId()).get(0);
		var exerciseStateWithMail = fakeEmailService.sendEmail(pageState6.getId(), mailExercise);
		assertEquals(((MailExercise)mailExercise).getMailBody(), exerciseStateWithMail.getInputState());
	}
}
