package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.state.PageState;

public interface PageStateRepository extends ArangoRepository<PageState> {

    @Query("FOR state IN pageState FILTER state.page == @pageId AND state.user == @userId RETURN state")
    PageState findByPageIdAndUserId(@Param("pageId") String pageId, @Param("userId") String userId);

    @Query("FOR state IN pageState FILTER state.gameState == @gameStateId RETURN state")
    List<PageState> findUserPageStates(@Param("gameStateId") String gameStateId);

    @Query("FOR state IN pageState FILTER state.topic == @0 AND state.user == @1 AND state.leftAt == null RETURN state")
    Optional<PageState> findCurrentPageStateForTopic(String topicId, String userId);
}
