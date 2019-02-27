package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.GameState;

public interface GameStateRepository extends ArangoRepository<GameState> {
    @Query("FOR state IN gameState FILTER state.user == @0 RETURN state")
    List<GameState> findByUserId(String userId);

    @Query("FOR state IN gameState FILTER state.user == @0 AND state.classroom == null AND state.finishedAt == null RETURN state")
    List<GameState> findNotFinishedStates(String userId);

    @Query("FOR state IN gameState FILTER state.user == @0 AND state.finishedAt != null RETURN state")
    List<GameState> findFinishedStates(String userId);

    @Query("FOR state IN gameState FILTER state.user == @0 AND state.classroom != null AND state.finishedAt == null RETURN state")
    List<GameState> findClassroomNotFinishedStates(String userId);

    @Query("FOR state IN gameState FILTER state._id == @0 RETURN state")
    Optional<GameState> findGameState(String gameStateId);
}


