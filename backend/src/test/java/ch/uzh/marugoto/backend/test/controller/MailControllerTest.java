package ch.uzh.marugoto.backend.test.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.state.MailReply;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class MailControllerTest extends BaseControllerTest {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private MailStateRepository mailStateRepository;
	@Autowired
	private NotificationRepository notificationRepository;

	private Mail mail;

	public synchronized void before() {
		super.before();
		var page6 = pageRepository.findByTitle("Page 6");
		mail = notificationRepository.findMailNotificationsForPage(page6.getId()).get(0);
		var mailState = new MailState(mail, user.getCurrentGameState());
		mailState.addMailReply(new MailReply("Mail replied"));
		mailStateRepository.save(mailState);
	}

	@Test
	public void testGetAllMails() throws Exception {
		mvc.perform(authenticate(get("/api/mail/list")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testSendReplyMail() throws Exception {
		var mailId = mail.getId();
		mvc.perform(authenticate(put("/api/mail/reply/" + mailId)
				.content("{ \"replyText\": \"Junit reply test\" }"))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}
}