
package ch.uzh.marugoto.backend.data.repository;


import com.arangodb.springframework.repository.ArangoRepository;
import ch.uzh.marugoto.backend.data.entity.PageState;

/**
 * Page state repository
 *
 */
public interface PageStateRepository extends ArangoRepository<PageState> {
	
	PageState findByPage(String pageId);
}
