package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.MailReply;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.helpers.StringHelper;

/**
 * Responsible for mails during the game that belongs to specific user (mail inbox)
 */
@Service
public class MailService {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private CriteriaService criteriaService;
    @Autowired
    private MailStateRepository mailStateRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Find mails that should be received on the current page
     * exclude mails that are already received by user
     *
     * @param user
     * @return mailList that should be received
     */
    public List<Mail> getIncomingMails(User user) {
        var pageId = user.getCurrentPageState().getPage().getId();

        List<Mail> incomingMails = notificationRepository.findMailNotificationsForPage(pageId).stream()
                .dropWhile(mail -> mailStateRepository.findMailState(user.getId(), mail.getId()).isPresent())
                .peek(mail -> mail.setBody(getFormattedText(mail.getBody(), user)))
                .collect(Collectors.toList());

        return incomingMails;
    }

    /**
     * Find all mails that user has received
     *
     * @param user
     * @return
     */
    public List<MailState> getReceivedMails(User user) {
        var receivedMails = mailStateRepository.findAllByUserId(user.getId());

        for (MailState mailState : receivedMails) {
            var mailBody = getFormattedText(mailState.getMail().getBody(), user);
            mailState.getMail().setBody(mailBody);
        }

        return receivedMails;
    }

    /**
     * Reply on mail
     *
     * @param user
     * @param mailId
     * @param replyText
     * @return
     */
    public MailState replyOnMail(User user, String mailId, String replyText) {
        MailState mailState = mailStateRepository.findMailState(user.getId(), mailId).orElseThrow();
        mailState.addMailReply(new MailReply(replyText));
        return save(mailState);
    }

    /**
     * Mail is received or mail has been read by user
     * When mail is received NotebookEntry is created
     *
     * @param mailId ID of mail notification
     * @param user current user
     */
    public MailState updateMailState(String mailId, User user, boolean isRead) {
        MailState mailState = mailStateRepository.findMailState(user.getId(), mailId).orElseGet(() -> new MailState(getMailNotification(mailId), user));

        if (!mailState.isRead()) {
            mailState.setRead(isRead);
            mailState = save(mailState);
            notebookService.createMailNotebookContent(mailState);
        }
        return mailState;
    }

    /**
     * Check weather mail has transition
     *
     * @param mailId mail ID
     * @param pageState current user PageState
     * @return pageTransition that should be triggered
     */
    public PageTransition getMailReplyTransition(String mailId, PageState pageState) {
        PageTransition pageTransition = null;
        for (PageTransitionState pageTransitionState : pageState.getPageTransitionStates()) {
            if (criteriaService.hasMailReplyCriteria(getMailNotification(mailId), pageTransitionState.getPageTransition())) {
                pageTransition = pageTransitionState.getPageTransition();
            }
        }

        return pageTransition;
    }

    /**
     * Finds mail notification by ID
     *
     * @param notificationId
     * @return notificationList
     */
    private Mail getMailNotification(String notificationId) {
        return notificationRepository.findMailNotification(notificationId).orElseThrow();
    }

    /**
     * Format mail body text, replace user placeholder
     * with real name
     *
     * @param mailBody mail body
     * @param user authenticated user
     * @return formatted text
     */
    private String getFormattedText(String mailBody, User user) {
        mailBody = StringHelper.replaceInText(mailBody, Constants.NOTIFICATION_TITLE_PLACEHOLDER, user.getSalutation().name());
        mailBody = StringHelper.replaceInText(mailBody, Constants.NOTIFICATION_FIRST_NAME_PLACEHOLDER, user.getFirstName());
        return mailBody;
    }

    /**
     * Save mail state to DB
     *
     * @param mailState
     * @return
     */
    private MailState save(MailState mailState) {
        return mailStateRepository.save(mailState);
    }
}
