package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Notification;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;

/**
 * Service responsible for notifications during the game
 */
@Service
public class NotificationService {

    @Autowired
    protected NotificationRepository notificationRepository;

    /**
     * Finds all incoming notifications for specific page
     *
     * @param page
     * @return
     */
    public List<Notification> getIncomingNotifications(Page page) {
        return notificationRepository.findByPageId(page.getId());
    }

    /**
     * Finds notification by ID
     *
     * @param notificationId
     * @return notificationList
     */
    public Notification getNotification(String notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow();
    }

    /**
     * Finds all mail notifications that should be received during game
     *
     * @return mailList
     */
    public List<Mail> getIncomingMails() {
        return notificationRepository.findMailNotifications();
    }

    /**
     * Finds all mail notifications that should be received on specific page
     *
     * @param page
     * @return mailList
     */
    public List<Mail> getIncomingMails(Page page) {
        return notificationRepository.findMailNotificationsForPage(page.getId());
    }

    /**
     * Finds all dialog notification that should be received on specific page
     *
     * @param page
     * @return dialogList
     */
    public List<Dialog> getIncomingDialogs(Page page) {
        return notificationRepository.findDialogNotificationsForPage(page.getId());
    }

    /**
     * Finds and replace {{user.name}} with current user, in mail notification body
     *
     * @param mail
     * @param user
     */
    protected void replaceUserNameTextInMailBody(Mail mail, User user) {
        String mailBody = mail.getBody();
        mail.setBody(mailBody.replace("{{user.name}}", user.getName()));
    }
}
