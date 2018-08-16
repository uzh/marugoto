package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.PageTransition;

public interface PageTransitionRepository extends ArangoRepository<PageTransition> {

}