package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.Component;

/**
 * 
 * Base Repository for all components
 * 
 */
public interface ComponentRepository extends ArangoRepository<Component> {
}
