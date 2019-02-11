package ch.uzh.marugoto.core.data.repository;

import java.util.List;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.state.PersonalNote;

public interface PersonalNoteRepository extends ArangoRepository<PersonalNote> {

    @Query("FOR note IN personalNote FILTER note.notebookEntry == @0 AND note.pageState == @1 RETURN note")
    List<PersonalNote> findAllPersonalNotes(String notebookEntryId, String pageStateId);
}
