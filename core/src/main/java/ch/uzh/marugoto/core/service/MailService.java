package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.UserMail;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Notification;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;

/**
 * Responsible for mails during the game that belongs to specific user (mail inbox)
 */
@Service
public class MailService extends NotificationService {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * Find mails that should be received on the current page
     * exclude mails that are already received by user
     *
     * @param pageState
     * @return
     */
    public List<Notification> getIncomingMails(PageState pageState) {
        return getIncomingMails(pageState.getPage()).stream()
                .dropWhile(mail -> userMailRepository.findByUserIdAndMailId(pageState.getUser().getId(), mail.getId()).isPresent())
                .peek(mail -> replaceUserNameTextInMailBody(mail, pageState.getUser()))
                .collect(Collectors.toList());
    }

    /**
     * Find all mails that user has received
     *
     * @param user
     * @return
     */
    public List<Mail> getReceivedMails(User user) {
        var receivedMails = new ArrayList<Mail>();

        for (UserMail userMail : userMailRepository.findAllByUserId(user.getId())) {
            Mail mail = userMail.getMail();
            replaceUserNameTextInMailBody(mail, user);
            mail.setReplied(userMail);
            receivedMails.add(mail);
        }

        return receivedMails;
    }

    /**
     * Reply on mail
     * adds new entry in userMail collection
     *
     * @param user
     * @param mailId
     * @param replyText
     * @return
     */
    public UserMail replyOnMail(User user, String mailId, String replyText) {
        Optional<UserMail> userMailOptional = userMailRepository.findByUserIdAndMailId(user.getId(), mailId);

        userMailOptional.ifPresent(userMail -> {
            userMail.setText(replyText);
            save(userMail);
        });

        return userMailOptional.orElseThrow();
    }

    /**
     * Mail is received or mail has been read by user
     * When mail is received, it is added inside userMail collection and notebook entry for mail should be created
     *
     * @param mailId
     * @param user
     */
    public UserMail syncMail(String mailId, User user, boolean isRead) {
        var userMail = userMailRepository.findByUserIdAndMailId(user.getId(), mailId).orElse(null);

        if (userMail == null) {
            var mail = (Mail) getNotification(mailId);
            notebookService.addNotebookEntryForMail(user.getCurrentPageState(), mail);
            userMail = new UserMail(mail, user);
        }

        userMail.setRead(isRead);
        return save(userMail);
    }

    /**
     * Simple save for UserMail
     *
     * @param userMail
     * @return
     */
    private UserMail save(UserMail userMail) {
        return userMailRepository.save(userMail);
    }
}
