package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.entity.Dialog;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Notification;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    protected NotificationRepository notificationRepository;

    public List<Notification> getPageNotifications(Page page) {
        return notificationRepository.findByPageId(page.getId());
    }

    public List<Mail> getMailNotifications() {
        List<Mail> mailNotifications = new ArrayList<>();
        notificationRepository.findAll().iterator().forEachRemaining(notification -> {
            if (notification instanceof Mail) {
                mailNotifications.add((Mail) notification);
            }
        });

        return mailNotifications;
    }

    public List<Mail> getMailNotifications(Page page) {
        return getPageNotifications(page).stream()
                .filter(notification -> notification instanceof Mail)
                .map(notification -> (Mail) notification)
                .collect(Collectors.toList());
    }

    public Mail getMailNotification(String mailId) {
        return (Mail) notificationRepository.findById(mailId).orElseThrow();
    }

    public List<Dialog> getDialogNotifications(Page page) {
        return getPageNotifications(page).stream()
                .filter(notification -> notification instanceof Dialog)
                .map(notification -> (Dialog) notification)
                .collect(Collectors.toList());
    }
}
