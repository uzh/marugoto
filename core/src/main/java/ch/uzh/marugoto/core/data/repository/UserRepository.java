package ch.uzh.marugoto.core.data.repository;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.application.User;

@Repository
public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
	User findByResetToken(String resetToken);
	@Query("FOR usr IN user FILTER usr.currentGameState == @0 "
				+ "REPLACE UNSET(usr, \"currentGameState\", \"currentPageState\") IN user "
					+ "RETURN NEW")
	User unsetUserStates(String gameStateid);
	
}