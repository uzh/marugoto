package ch.uzh.marugoto.core.data.repository;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.User;

@Repository
public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
	User findByResetToken(String resetToken);
}