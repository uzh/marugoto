package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.DialogState;

public interface DialogStateRepository extends ArangoRepository<DialogState> {

    @Query("FOR state IN dialogState FILTER state.gameState == @gameStateId AND state.dialogResponse == @responseId RETURN state")
    Optional<DialogState> findDialogStateByResponse(@Param("gameStateId") String gameStateId, @Param("responseId") String dialogResponseId);

    @Query("FOR state IN dialogState FILTER state.gameState == @gameStateId AND state.dialogSpeech == @speechId RETURN state")
    Optional<DialogState> findDialogStateByDialogSpeech(@Param("gameStateId") String gameStateId, @Param("speechId") String dialogSpeechId);
}
