package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Notification;
import ch.uzh.marugoto.core.data.entity.PageState;
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
     * @param pageState
     * @return
     */
    public List<Notification> getIncomingMails(PageState pageState) {
        return getIncomingMails(pageState.getPage()).stream()
                .dropWhile(mail -> userMailRepository.findByUserIdAndMailId(pageState.getUser().getId(), mail.getId()).isPresent())
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

        for (Mail mail : getIncomingMails()) {
            userMailRepository.findByUserIdAndMailId(user.getId(), mail.getId()).ifPresent(userMail -> {
                mail.setReplied(userMail);
                receivedMails.add(mail);
            });
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
        Optional<UserMail> userMailOptional = userMailRepository.findByUserIdAndMailId(user.getId(), mailId);
        UserMail userMail;

        if (userMailOptional.isPresent()) {
            userMail = userMailOptional.get();
            userMail.setText(replyText);
        } else {
            Mail mail = (Mail) getNotification(mailId);
            userMail = new UserMail(mail, user, replyText);
        }

        return save(userMail);
    }

    public void receiveMail(String mailId, User user) {
        var mail = (Mail) getNotification(mailId);
        notebookService.addNotebookEntryForMail(user.getCurrentPageState(), mail);
        save(new UserMail(mail, user));
    }

    private UserMail save(UserMail userMail) {
        return userMailRepository.save(userMail);
    }
}
