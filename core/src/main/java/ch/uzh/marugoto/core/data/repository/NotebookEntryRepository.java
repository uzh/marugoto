package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreationTime;

public interface NotebookEntryRepository extends ArangoRepository<NotebookEntry> {

    @Query("FOR entry IN notebookEntry FILTER entry.page == @pageId AND entry.notebookEntryCreationTime == @notebookEntryCreationTime RETURN entry")
    NotebookEntry findByPageAndCreationTime(@Param("pageId") String pageId, @Param("notebookEntryCreationTime") NotebookEntryCreationTime notebookEntryCreationTime);
}
