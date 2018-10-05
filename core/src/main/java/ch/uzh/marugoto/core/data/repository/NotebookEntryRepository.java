package ch.uzh.marugoto.core.data.repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.repository.query.Param;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;

public interface NotebookEntryRepository extends ArangoRepository<NotebookEntry> {

    @Query("FOR entry IN notebookEntry FILTER entry.page == @pageId AND entry.createAt == @notebookEntryCreateAt RETURN entry")
    NotebookEntry findNotebookEntryByCreationTime(@Param("pageId") String pageId, @Param("notebookEntryCreateAt") NotebookEntryCreateAt notebookEntryCreateAt);
}
