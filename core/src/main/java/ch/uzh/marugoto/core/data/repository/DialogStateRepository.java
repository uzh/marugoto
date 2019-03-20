package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.DialogState;

public interface DialogStateRepository extends ArangoRepository<DialogState> {

    @Query("FOR state IN dialogState FILTER state.user == @0 AND state.dialogResponse == @1 RETURN state")
    Optional<DialogState> findDialogState(String userId, String dialogResponseId);

    Optional<DialogState> findDialogStateByDialogSpeechId(String dialogSpeechId);
}
