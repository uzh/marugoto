package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.topic.Component;

/**
 * Base Repository for all components.
 */
public interface ComponentRepository extends ArangoRepository<Component> {

    @Query("FOR cmp IN component FILTER cmp.page == @pageId SORT cmp.renderOrder RETURN cmp")
    List<Component> findPageComponents(@Param("pageId") String pageId);
}
