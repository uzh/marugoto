package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.UserMail;

public interface UserMailRepository extends ArangoRepository<UserMail> {

    Optional<UserMail> findUserMailByMailId(String mailId);

    @Query("FOR um IN userMail FILTER um.mail == @1 " +
            "FOR ps IN pageState FILTER ps.user == @0 AND um.pageState == ps._id RETURN um")
    List<UserMail> findByUserIdAndMailId(String userId, String mailId);
}
