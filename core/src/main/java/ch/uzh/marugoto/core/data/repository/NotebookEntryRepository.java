package ch.uzh.marugoto.core.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;

public interface NotebookEntryRepository extends ArangoRepository<NotebookEntry> {

    @Query("FOR entry IN notebookEntry FILTER entry.page == @pageId AND entry.addToPageStateAt == @addToPageStateAt RETURN entry")
    Optional<NotebookEntry> findNotebookEntryByCreationTime(@Param("pageId") String pageId, @Param("addToPageStateAt") NotebookEntryAddToPageStateAt addToPageStateAt);

    @Query(
            "FOR state in pageState " +
                "FILTER state.user == @userId " +
                "FOR entryId IN state.notebookEntries " +
                    "FOR entry IN notebookEntry FILTER entryId == entry._id " +
                    "RETURN entry"
    )
    List<NotebookEntry> findUserNotebookEntries(@Param("userId") String userId);
    
    @Query("FOR entry IN notebookEntry FILTER entry.dialogResponse == @dialogResponseId RETURN entry")
    Optional<NotebookEntry> findNotebookEntryByDialogResponse(@Param("dialogResponseId") String dialogResponseId);
    
    @Query("FOR entry IN notebookEntry FILTER entry.mailExercise == @mailExerciseId RETURN entry")
    Optional<NotebookEntry> findNotebookEntryByMailExercise(@Param("mailExerciseId") String mailExerciseId);
}
