package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DialogResponseRepository extends ArangoRepository<DialogResponse> {

    @Query("FOR dialogSpeech, dialogResponse IN OUTBOUND @fromDialogSpeechId dialogResponse RETURN dialogResponse")
    List<DialogResponse> findByDialogSpeech(@Param("fromDialogSpeechId") String fromDialogSpeech);
}
