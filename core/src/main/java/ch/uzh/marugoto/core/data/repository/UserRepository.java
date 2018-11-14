package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.stereotype.Repository;

import ch.uzh.marugoto.core.data.entity.User;

@Repository
public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
	User findByResetToken(String resetToken);
}