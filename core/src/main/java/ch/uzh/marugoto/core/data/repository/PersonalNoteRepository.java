package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.PersonalNote;

public interface PersonalNoteRepository extends ArangoRepository<PersonalNote> {}
