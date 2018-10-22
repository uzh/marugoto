package ch.uzh.marugoto.core.data.repository;

import java.util.List;
import java.util.Optional;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.ExerciseState;

public interface ExerciseStateRepository extends ArangoRepository<ExerciseState> {

	@Query("FOR state IN exerciseState FILTER state.pageState == @0 RETURN state")
	List<ExerciseState> findAllUserExerciseStates(String pageStateId);

	@Query("FOR state IN exerciseState FILTER state.pageState == @0 AND state.exercise == @1 RETURN state")
    Optional<ExerciseState> findUserExerciseState(String pageStateId, String exerciseId);
}
