package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;

public interface NotebookEntryStateRepository extends ArangoRepository<NotebookEntryState> {

    @Query("FOR state in notebookEntryState FILTER state.gameState == @gameStateId SORT state.createdAt RETURN state")
    List<NotebookEntryState> findUserNotebookEntryStates(@Param("gameStateId") String gameStateId);

    @Query("FOR state in notebookEntryState FILTER state.gameState == @gameStateId SORT state.createdAt DESC LIMIT 1 RETURN state")
    NotebookEntryState findLastNotebookEntryState(@Param("gameStateId") String gameStateId);

    Optional<NotebookEntryState> findNotebookEntryStateById(String notebookEntryStateId);
}
