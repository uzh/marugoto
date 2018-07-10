package ch.uzh.marugoto.data.repository;

import ch.uzh.marugoto.data.entity.Character;
import com.arangodb.springframework.repository.ArangoRepository;
 
public interface CharacterRepository extends ArangoRepository<Character> {
 
}