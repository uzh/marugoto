package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.UserMail;

public interface UserMailRepository extends ArangoRepository<UserMail> {

    @Query("FOR um IN userMail FILTER um.user == @0 AND um.mail == @1 RETURN um")
    Optional<UserMail> findByUserIdAndMailId(String userId, String mailId);
}
