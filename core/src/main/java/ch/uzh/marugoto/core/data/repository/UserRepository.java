package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.UserType;

@Repository
public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
	User findByResetToken(String resetToken);
	List<User> findAllByTypeIsNot(UserType userTypeToExclude);
}