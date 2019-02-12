package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.Component;

/**
 * Base Repository for all components.
 */
public interface ComponentRepository extends ArangoRepository<Component> {

    List<Component> findByPageIdOrderByRenderOrderAsc(String pageId);
}
