package ch.uzh.marugoto.core.data.repository;

import org.springframework.data.repository.query.Param;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;


public interface PageTransitionStateRepository extends ArangoRepository<PageTransitionState> {
	
	@Query("FOR state IN pageTransitionState FILTER state.pageTransition == @pageTransitionId RETURN state")
	PageTransitionState findByPageTransition(@Param("pageTransitionId") String pageTransitionId);
}