package ch.uzh.marugoto.core.test.repository;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.PersonalNote;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PersonalNoteRepositoryTest extends BaseCoreTest {

    @Autowired
    private PersonalNoteRepository personalNoteRepository;

    @Test
    public void testSave() {
        var text = "test note";
        var personalNote = personalNoteRepository.save(new PersonalNote(text));
        assertNotNull(personalNote);
        assertEquals(text, personalNote.getMarkdownContent());
    }
}
