package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.Component;

/**
 * Base Repository for all components.
 */
public interface ComponentRepository extends ArangoRepository<Component> {
}
