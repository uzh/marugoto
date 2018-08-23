package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PageState;

/**
 * Page state repository
 *
 */
public interface PageStateRepository extends ArangoRepository<PageState> {
	
	PageState findByPage(String pageId);
}
