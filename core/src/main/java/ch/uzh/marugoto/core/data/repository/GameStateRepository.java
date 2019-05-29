package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

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

    @Query("FOR state IN gameState FILTER state.user == @userId AND state.topic == @topicId AND state.finishedAt == null RETURN state")
    Optional<GameState> findNotFinishedGameStateByTopic(@Param("userId") String userId, @Param("topicId") String topicId);
    
    @Query("FOR state IN gameState FILTER state.topic == @0 RETURN state")
    List<GameState> findByTopicId(String topicId);
    
    @Query("FOR state IN gameState FILTER state.user == @userId AND state.topic == @topicId RETURN state")
    List<GameState> findByTopicAndUser(@Param("userId") String userId, @Param("topicId") String topicId);
    
    Optional<GameState> findByClassroomAndUser(String classroomId, String userId);
    
}

