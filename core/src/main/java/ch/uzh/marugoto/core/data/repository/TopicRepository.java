package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.topic.Topic;

public interface TopicRepository extends ArangoRepository<Topic> {

    List<Topic> findByActiveIsTrue();
}
