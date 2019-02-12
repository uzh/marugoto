package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.Character;

public interface CharacterRepository extends ArangoRepository<Character> {
}
