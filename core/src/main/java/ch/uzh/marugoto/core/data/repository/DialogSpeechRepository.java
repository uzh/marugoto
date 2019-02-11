package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;

public interface DialogSpeechRepository extends ArangoRepository<DialogSpeech> {
}
