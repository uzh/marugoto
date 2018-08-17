package ch.uzh.marugoto.backend.data.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.PageTransition;

public interface PageTransitionRepository extends ArangoRepository<PageTransition> {
	
	@Query("FOR page, pageTransition IN OUTBOUND @fromPageId pageTransition RETURN pageTransition")
	List<PageTransition> getPageTransitionsByPageId(@Param("fromPageId") String fromPageId);

}