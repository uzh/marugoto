package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.StorylineState;

public interface StorylineStateRepository extends ArangoRepository<StorylineState> {
    @Query("FOR state IN storylineState FILTER state.user == @0 RETURN state")
    StorylineState findByUserId(String id);
}


