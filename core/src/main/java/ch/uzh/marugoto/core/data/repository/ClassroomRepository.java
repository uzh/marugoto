package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.application.Classroom;

public interface ClassroomRepository extends ArangoRepository<Classroom> {
}
