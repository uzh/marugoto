package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserMail;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;

@Service
public class MailService extends NotificationService {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * Finds mails that should be received on current page
     *
     * @param page
     * @return
     */
    public List<Mail> getIncomingMails(Page page) {
        return getPageNotifications(page).stream()
                .filter(notification -> notification instanceof Mail)
                .map(notification -> (Mail) notification)
                .collect(Collectors.toList());
    }

    /**
     * Finds mails that user received
     *
     * @param user
     * @return
     */
    public List<Mail> getReceivedMails(User user) {
        var receivedMails = new ArrayList<Mail>();

        for (Mail mail : getMailNotifications()) {
            List<UserMail> userMails = userMailRepository.findByUserIdAndMailId(user.getId(), mail.getId());

            if (userMails.size() > 0) {
                mail.setReplies(userMails);
                receivedMails.add(mail);
            }
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
    public UserMail replyOnMail(User user, String mailId, String replyText) {
        Mail mail = (Mail) getNotification(mailId);
        return save(new UserMail(mail, user.getCurrentPageState(), replyText));
    }

    public void receiveMail(String mailId, User user) {
        var mail = (Mail) getNotification(mailId);
        notebookService.addNotebookEntryForMail(user.getCurrentPageState(), mail);
        save(new UserMail(mail, user.getCurrentPageState()));
    }

    private UserMail save(UserMail userMail) {
        return userMailRepository.save(userMail);
    }
}
