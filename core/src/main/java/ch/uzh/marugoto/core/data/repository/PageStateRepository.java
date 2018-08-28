package ch.uzh.marugoto.core.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PageState;

/**
 * Page state repository.
 */
public interface PageStateRepository extends ArangoRepository<PageState> {

	List<PageState> findByUser(@Param("userId") String userId);
	
	@Query("FOR state IN pageState FILTER state.page == @pageId && state.user == @userId RETURN state")
	Optional<PageState> findByPageAndUser(@Param("pageId") String pageId, @Param("userId") String userId);
}
