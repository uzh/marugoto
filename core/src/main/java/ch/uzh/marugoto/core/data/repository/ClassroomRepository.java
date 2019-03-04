package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.application.Classroom;

public interface ClassroomRepository extends ArangoRepository<Classroom> {

    @Query("FOR class IN classroom FILTER class.invitationLinkId == @0 RETURN class")
    Optional<Classroom> findByInvitationLink(String invitationLink);
}
