package ch.uzh.marugoto.backend.test.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.UserMail;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;
import ch.uzh.marugoto.core.service.NotificationService;
import ch.uzh.marugoto.core.service.PageStateService;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NotificationControllerTest extends BaseControllerTest {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private UserMailRepository userMailRepository;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private NotificationService notificationService;

	private PageState pageState6;
	private Mail mail;

	public synchronized void before() {
		super.before();
		var page6 = pageRepository.findByTitle("Page 6");
		pageState6 = pageStateService.initializeStateForNewPage(page6, user);
		mail = notificationService.getMailNotifications(pageState6.getPage()).get(0);
		var repliedMail = new UserMail(mail, pageState6, "Mail replied");
		userMailRepository.save(repliedMail);
	}

	@Test
	public void testGetAllMails() throws Exception {
		mvc.perform(authenticate(get("/api/mail/list")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testSendReplyMail() throws Exception {
		var mailId = mail.getId();
		mvc.perform(authenticate(put("/api/mail/send/" + mailId).param("replyText", "Junit reply test"))).andExpect(status().isOk());
	}
}