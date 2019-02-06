package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Notification;

public interface NotificationRepository extends ArangoRepository<Notification> {

    List<Notification> findByPageId(String id);

    @Query("FOR mail in notification FILTER mail._class == 'ch.uzh.marugoto.core.data.entity.Mail' RETURN mail")
    List<Mail> findMailNotifications();
}
