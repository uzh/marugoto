package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.Page;

/**
 * repository page
 * 
 * @author Christian
 */

public interface PageRepository extends ArangoRepository<Page> {

}