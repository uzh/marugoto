package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.Storyline;;

/**
 * repository for Storyline
 * 
 */
public interface StorylineRepository extends ArangoRepository<Storyline> {

}