package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.Resource;

public interface ResourceRepository extends ArangoRepository<Resource> {
	Resource findByPath(String filePath);
}
