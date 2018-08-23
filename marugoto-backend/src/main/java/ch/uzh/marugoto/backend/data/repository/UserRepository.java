package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.User;

public interface UserRepository extends ArangoRepository<User> {
	
	User findByMail(String mail);
}