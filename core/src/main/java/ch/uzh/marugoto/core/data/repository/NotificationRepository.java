package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Notification;

public interface NotificationRepository extends ArangoRepository<Notification> {

    List<Notification> findByPageId(String id);
}
