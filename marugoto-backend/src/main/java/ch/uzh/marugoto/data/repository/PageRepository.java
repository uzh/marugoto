package ch.uzh.marugoto.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.data.entity.Page;
 
public interface PageRepository extends ArangoRepository<Page> {
 
}