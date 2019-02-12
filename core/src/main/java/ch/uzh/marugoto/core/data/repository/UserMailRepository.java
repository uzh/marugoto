package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.state.UserMail;

import java.util.List;
import java.util.Optional;

public interface UserMailRepository extends ArangoRepository<UserMail> {

    List<UserMail> findAllByUserId(String userId);

    @Query("FOR um IN userMail FILTER um.user == @0 AND um.mail == @1 RETURN um")
    Optional<UserMail> findByUserIdAndMailId(String userId, String mailId);
}
