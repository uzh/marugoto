package ch.uzh.marugoto.core.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.state.DialogState;

public interface DialogStateRepository extends ArangoRepository<DialogState> {

    @Query("FOR state IN dialogState FILTER state.gameState == @gameStateId AND state.dialogResponse == @responseId RETURN state")
    Optional<DialogState> findDialogStateByResponse(@Param("gameStateId") String gameStateId, @Param("responseId") String dialogResponseId);

    @Query("FOR state IN dialogState FILTER state.gameState == @gameStateId AND state.dialogSpeech == @speechId RETURN state")
    Optional<DialogState> findDialogStateByDialogSpeech(@Param("gameStateId") String gameStateId, @Param("speechId") String dialogSpeechId);
    
    @Query("FOR state IN dialogState FILTER state.gameState == @gameStateId RETURN state")
    List<DialogState> findByGameState(@Param("gameStateId") String gameStateId);
}
