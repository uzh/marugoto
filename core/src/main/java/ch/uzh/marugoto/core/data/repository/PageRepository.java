package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.Page;

/**
 * repository page
 * 
 * @author Christian
 */
public interface PageRepository extends ArangoRepository<Page> {
	
	Page findByTitle(String pageName);
}