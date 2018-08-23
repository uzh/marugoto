package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import ch.uzh.marugoto.backend.data.entity.ExerciseState;

/**
 * 
 * Exercise state repository
 *
 */
public interface ExerciseStateRepository extends ArangoRepository<ExerciseState> {}
