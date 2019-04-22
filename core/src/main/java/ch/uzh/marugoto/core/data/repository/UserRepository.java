package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.application.UserType;

@Repository
public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
	User findByResetToken(String resetToken);
	List<User> findAllByTypeIsNot(UserType userTypeToExclude);
}