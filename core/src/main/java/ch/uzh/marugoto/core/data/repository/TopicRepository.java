package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.Topic;

public interface TopicRepository extends ArangoRepository<Topic> {
	
}
