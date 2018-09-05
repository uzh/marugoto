package ch.uzh.marugoto.core.data.repository;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PageState;

public interface PageStateRepository extends ArangoRepository<PageState> {
	
	@Query("FOR state IN pageState FILTER state.page == @pageId && state.user == @userId RETURN state")
	PageState findByPageAndUser(@Param("pageId") String pageId, @Param("userId") String userId);
}
