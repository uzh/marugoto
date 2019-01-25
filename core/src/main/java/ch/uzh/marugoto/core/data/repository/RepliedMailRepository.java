package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.RepliedMail;

public interface RepliedMailRepository extends ArangoRepository<RepliedMail> {

    List<RepliedMail> findByPageStateUserId(String userId);
    @Query("FOR rm IN repliedMail FILTER rm.mail == @1 " +
            "FOR ps IN pageState FILTER ps.user == @0 RETURN rm")
    Optional<RepliedMail> findByUserIdAndMailId(String userId, String mailId);
}
