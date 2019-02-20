package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailReply;
import ch.uzh.marugoto.core.data.entity.topic.Page;
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
     * @param pageState
     * @return
     */
    public List<Mail> getMailNotifications(PageState pageState) {
        return getMailNotificationsFromRepository(pageState.getPage()).stream()
            .dropWhile(mail -> mailStateRepository.findMailState(pageState.getUser().getId(), mail.getId()).isPresent())
            .peek(mail -> mail.setBody(StringHelper.replaceInText(mail.getBody(), Constants.NOTIFICATION_USER_PLACEHOLDER, pageState.getUser().getName())))
            .collect(Collectors.toList());
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
            var mailBody = StringHelper.replaceInText(mailState.getMail().getBody(), Constants.NOTIFICATION_USER_PLACEHOLDER, user.getName());
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
        MailState mailState = mailStateRepository.findMailState(user.getId(), mailId).orElseGet(() -> {
            Mail mail = getMailNotificationFromRepository(mailId);
            return new MailState(mail, user);
        });;

        mailState.addMailReply(new MailReply(replyText));
        return save(mailState);
    }

    /**
     * Mail is received or mail has been read by user
     * When mail is received mail state and notebook entry should be created
     *
     * @param mailId
     * @param user
     */
    public MailState syncMail(String mailId, User user, boolean isRead) {
        MailState mailState = mailStateRepository.findMailState(user.getId(), mailId).orElseGet(() -> {
            Mail mail = getMailNotificationFromRepository(mailId);
            notebookService.addNotebookEntryForMail(user.getCurrentPageState(), mail);
            return new MailState(mail, user);
        });

        mailState.setRead(isRead);
        return save(mailState);
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
            if (criteriaService.hasMailReplyCriteria(getMailNotificationFromRepository(mailId), pageTransitionState.getPageTransition())) {
                pageTransition = pageTransitionState.getPageTransition();
            }
        }

        return pageTransition;
    }

    /**
     * Finds all mail notifications that should be received on specific page
     *
     * @param page
     * @return mailList
     */
    private List<Mail> getMailNotificationsFromRepository(Page page) {
        return notificationRepository.findMailNotificationsForPage(page.getId());
    }

    /**
     * Finds mail notification by ID
     *
     * @param notificationId
     * @return notificationList
     */
    private Mail getMailNotificationFromRepository(String notificationId) {
        return notificationRepository.findMailNotification(notificationId).orElseThrow();
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
