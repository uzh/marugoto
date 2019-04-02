package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.MailState;

public interface MailStateRepository extends ArangoRepository<MailState> {

    @Query("FOR state IN mailState " +
                "FILTER state.gameState == @gameStateId " +
                "SORT state.createdAt DESC " +
            "RETURN state")
    List<MailState> findAllForGameState(@Param("gameStateId") String gameStateId);

    @Query("FOR state IN mailState FILTER state.gameState == @gameStateId AND state.mail == @mailId RETURN state")
    Optional<MailState> findMailState(@Param("gameStateId") String gameStateId, @Param("mailId") String mailId);
}
