package ch.uzh.marugoto.core.data.repository;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PageState;

/**
 * Page state repository.
 */
public interface PageStateRepository extends ArangoRepository<PageState> {

	@Query("FOR ps IN pageState FILTER ps.page == @pageId AND ps.user == @userId RETURN ps")
	PageState findByPageAndUser(@Param("pageId") String pageId, @Param("userId") String userId);
}
