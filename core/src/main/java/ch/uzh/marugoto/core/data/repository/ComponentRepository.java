package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;

/**
 * Base Repository for all components.
 */
public interface ComponentRepository extends ArangoRepository<Component> {

    List<Component> findByPageId(String pageId);
}
