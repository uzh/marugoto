package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Notification;

public interface NotificationRepository extends ArangoRepository<Notification> {

    @Query("FOR mail in notification FILTER mail.page == @0 AND mail._class LIKE '%Mail' RETURN mail")
    List<Mail> findMailNotificationsForPage(String pageId);

    @Query(
            "FOR mail in notification FILTER mail._class LIKE '%Mail' AND mail.page == @0 " +
                "FOR state IN mailState FILTER state.user == @1 AND state.mail != mail._id " +
            "RETURN mail"
    )
    List<Mail> findIncomingMailsForPage(String pageId, String userId);

    @Query("FOR mail in notification FILTER mail._id == @0 RETURN mail")
    Optional<Mail> findMailNotification(String mailId);

    @Query("FOR dialog IN notification FILTER dialog.page == @0 AND dialog._class LIKE '%Dialog' RETURN dialog")
    List<Dialog> findDialogNotificationsForPage(String pageId);
}
