package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.RepliedMail;
import ch.uzh.marugoto.core.data.entity.Notification;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.RepliedMailRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.exception.RepliedMailNotFoundException;

@Service
public class NotificationService implements ReplyMailService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RepliedMailRepository replyMailRepository;

    public List<Notification> getPageNotifications(Page page) {
        return notificationRepository.findByPageId(page.getId());
    }

    public List<Mail> getMails(Page page) {
        return getPageNotifications(page).stream()
                .filter(notification -> notification instanceof Mail)
                .map(notification -> (Mail) notification)
                .collect(Collectors.toList());
    }

    public Mail findMailById(String mailId) {
        return (Mail) notificationRepository.findById(mailId).orElseThrow();
    }

    @Override
    public RepliedMail sendReplyMail(User user, Mail mail, String replyText) {
        RepliedMail replyMail = new RepliedMail(mail, user.getCurrentPageState(), replyText);
        return saveRepliedMail(replyMail);
    }

    @Override
    public List<RepliedMail> getAllRepliedMails(User user) {
        return replyMailRepository.findByPageStateUserId(user.getId());
    }

    @Override
    public RepliedMail getRepliedMail(User user, String mailId) throws RepliedMailNotFoundException {
        return replyMailRepository.findByUserIdAndMailId(user.getId(), mailId).orElseThrow(RepliedMailNotFoundException::new);
    }

    @Override
    public RepliedMail saveRepliedMail(RepliedMail replyMail) {
        return replyMailRepository.save(replyMail);
    }
}
