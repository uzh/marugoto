package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;
import ch.uzh.marugoto.core.data.entity.state.GameState;

public interface GameStateRepository extends ArangoRepository<GameState> {
    @Query("FOR state IN gameState FILTER state.user == @0 RETURN state")
    GameState findByUserId(String id);
}


