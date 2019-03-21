package ch.uzh.marugoto.core.data.repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;

public interface NotebookEntryRepository extends ArangoRepository<NotebookEntry> {

    Optional<NotebookEntry> findByMailId(String mailId);

    @Query("FOR entry IN notebookEntry FILTER entry.page == @pageId RETURN entry")
    Optional<NotebookEntry> findNotebookEntryByPage(@Param("pageId") String pageId);
    
    @Query("FOR entry IN notebookEntry FILTER entry.dialogResponse == @dialogResponseId RETURN entry")
    Optional<NotebookEntry> findNotebookEntryByDialogResponse(@Param("dialogResponseId") String dialogResponseId);
}
