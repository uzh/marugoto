package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.MailState;

public interface MailStateRepository extends ArangoRepository<MailState> {

    @Query("FOR mailState IN mailState " +
                "FILTER mailState.user == @0 " +
                "SORT mailState.createdAt DESC " +
            "RETURN mailState")
    List<MailState> findAllByUserId(String userId);

    @Query("FOR mailState IN mailState " +
                "FILTER mailState.user == @0 AND mailState.mail == @1 " +
            "RETURN mailState")
    Optional<MailState> findMailState(String userId, String mailId);
}
