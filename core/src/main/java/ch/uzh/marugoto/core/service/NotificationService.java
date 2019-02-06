package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Notification getNotification(String notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow();
    }

    public List<Mail> getMailNotifications() {
        return notificationRepository.findMailNotifications();
    }
}
