package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserMail;
import ch.uzh.marugoto.core.data.repository.UserMailRepository;

@Service
public class UserMailService extends NotificationService {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private UserMailRepository userMailRepository;


    public List<Mail> getAllMailsWithUserReplies(User user) {
        var mails = getMailNotifications();

        for (Mail mail : mails) {
            List<UserMail> repliedMails = userMailRepository.findByUserIdAndMailId(user.getId(), mail.getId());
            mail.setReplies(repliedMails);
        }

        return mails;
    }

    public UserMail replyMail(User user, String mailId, String replyText) {
        Mail mail = getMailNotification(mailId);
        UserMail userMail = saveMail(new UserMail(mail, user.getCurrentPageState(), replyText));
        notebookService.addNotebookEntryForMail(user.getCurrentPageState(), mail);
        return saveMail(userMail);
    }

    public UserMail saveMail(UserMail userMail) {
        return userMailRepository.save(userMail);
    }
}
