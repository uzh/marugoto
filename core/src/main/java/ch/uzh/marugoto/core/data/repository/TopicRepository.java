package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.Topic;

import java.util.List;

public interface TopicRepository extends ArangoRepository<Topic> {

    List<Topic> findByActiveIsTrue();
}
