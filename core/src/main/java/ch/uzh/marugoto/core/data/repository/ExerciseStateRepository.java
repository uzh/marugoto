package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.ExerciseState;

/**
 * 
 * Exercise state repository
 *
 */
public interface ExerciseStateRepository extends ArangoRepository<ExerciseState> {
	
}
