package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PersonalNote;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;

@Service
public class NotebookService {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;

    @Autowired
    private PersonalNoteRepository personalNoteRepository;


    /**
     * Finds notebook entry
     *
     * @param page
     * @param notebookEntryCreateAt
     * @return notebookEntry
     */
    public NotebookEntry getNotebookEntry(Page page, NotebookEntryCreateAt notebookEntryCreateAt) {
        return notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), notebookEntryCreateAt);
    }

    /**
     * Creates user personal note
     * @param text
     * @param user
     * @return personalNote
     * @throws PageStateNotFoundException
     */
    public PersonalNote createPersonalNote(String text, User user) throws PageStateNotFoundException {
        if (user.getCurrentlyAt() == null) {
            throw new PageStateNotFoundException();
        }

        PersonalNote personalNote = new PersonalNote(text);
        personalNote.setNoteFrom(user.getCurrentlyAt());
        personalNoteRepository.save(personalNote);

        return personalNote;
    }

    /**
     * Finds personal note by ID
     *
     * @param personalNoteId
     * @return personalNote
     */
    public PersonalNote getPersonalNote(String personalNoteId) {
        return personalNoteRepository.findById(personalNoteId).orElseThrow();
    }

    /**
     * Updates personal note
     *
     * @param id
     * @param text
     * @return personalNote
     */
    public PersonalNote updatePersonalNote(String id, String text) {
        PersonalNote personalNote = personalNoteRepository.findById(id).orElseThrow();
        personalNote.setMarkdownContent(text);
        personalNoteRepository.save(personalNote);
        return personalNote;
    }

    /**
     * Deletes personal note
     *
     * @param id
     */
    public void deletePersonalNote(String id) {
        PersonalNote personalNote = personalNoteRepository.findById(id).orElseThrow();
        personalNoteRepository.delete(personalNote);
    }
}
