package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.ClassroomMember;
import ch.uzh.marugoto.core.data.entity.application.User;

public interface ClassroomMemberRepository extends ArangoRepository<ClassroomMember> {
    @Query("FOR user, classroomMember IN OUTBOUND @0 classroomMember RETURN user")
    List<User> findClassroomMembers(String classroomId);

    @Query("FOR user, classroomMember IN OUTBOUND @classId classroomMember FILTER user._id == @userId RETURN user")
    User findMemberOfClassroom(@Param("userId") String userId, @Param("classId") String classroomId);
}
