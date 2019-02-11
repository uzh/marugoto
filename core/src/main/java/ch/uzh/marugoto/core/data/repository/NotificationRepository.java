package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Notification;

public interface NotificationRepository extends ArangoRepository<Notification> {

    List<Notification> findByPageId(String id);

    @Query("FOR mail in notification FILTER mail._class == 'ch.uzh.marugoto.core.data.entity.Mail' RETURN mail")
    List<Mail> findMailNotifications();

    @Query("FOR mail in notification FILTER mail.page == @0 AND mail._class == 'ch.uzh.marugoto.core.data.entity.Mail' RETURN mail")
    List<Mail> findMailNotificationsForPage(String pageId);

    @Query("FOR dialog IN notification FILTER dialog.page == @0 AND dialog._class == 'ch.uzh.marugoto.core.data.entity.Dialog' RETURN dialog")
    List<Dialog> findDialogNotificationsForPage(String pageId);
}
