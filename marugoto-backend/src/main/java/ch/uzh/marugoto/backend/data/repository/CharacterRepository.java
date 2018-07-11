package ch.uzh.marugoto.backend.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.backend.data.entity.Character;
 
public interface CharacterRepository extends ArangoRepository<Character> {
 
}