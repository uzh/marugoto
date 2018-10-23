package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.PageState;

public interface PageStateRepository extends ArangoRepository<PageState> {

    @Query("FOR state IN pageState FILTER state.page == @pageId AND state.user == @userId RETURN state")
    PageState findByPageIdAndUserId(@Param("pageId") String pageId, @Param("userId") String userId);

    @Query("FOR state IN pageState FILTER state.user == @userId RETURN state")
    List<PageState> findUserPageStates(@Param("userId") String userId);

    @Query(
            "FOR state in pageState " +
                "FILTER state.user == @userId " +
                "FOR entry IN state.notebookEntries " +
                "RETURN entry"
    )
    List<NotebookEntry> findUserNotebookEntries(@Param("userId") String userId);
}
