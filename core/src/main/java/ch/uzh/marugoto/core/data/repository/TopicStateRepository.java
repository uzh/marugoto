package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.state.TopicState;

public interface TopicStateRepository extends ArangoRepository<TopicState> {
    @Query("FOR state IN topicState FILTER state.user == @0 RETURN state")
    TopicState findByUserId(String id);
}


