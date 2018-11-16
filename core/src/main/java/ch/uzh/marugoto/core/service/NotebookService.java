package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PersonalNote;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;

@Service
public class NotebookService {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private PersonalNoteRepository personalNoteRepository;
    @Autowired
    private PageStateRepository pageStateRepository;
    @Autowired
    private Messages messages;


    /**
     * Finds notebook entries by ids
     *
     * @param pageState
     * @return notebookEntries list
     */
    public Iterable<NotebookEntry> getNotebookEntries(PageState pageState) {
        return notebookEntryRepository.findAllById(pageState.getNotebookEntries());
    }

    /**
     * Finds notebook entry
     *
     * @param page
     * @param notebookEntryCreateAt
     * @return notebookEntry
     */
    public Optional<NotebookEntry> getNotebookEntry(Page page, NotebookEntryCreateAt notebookEntryCreateAt) {
        return notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), notebookEntryCreateAt);
    }

    public void addNotebookEntry(PageState currentPageState, NotebookEntryCreateAt notebookEntryCreateAt) {
        getNotebookEntry(currentPageState.getPage(), notebookEntryCreateAt).ifPresent(notebookEntry -> {
            currentPageState.addNotebookEntry(notebookEntry);
            pageStateRepository.save(currentPageState);
        });
    }

    /**
     * Creates user personal note
     * 
     * @param markdownContent
     * @param user
     * @return personalNote
     * @throws PageStateNotFoundException
     */
    public PersonalNote createPersonalNote(String markdownContent, User user) throws PageStateNotFoundException {
        if (user.getCurrentPageState() == null) {
            throw new PageStateNotFoundException(messages.get("pageStateNotFound"));
        }

        PersonalNote personalNote = new PersonalNote(markdownContent);
        personalNote.setPageState(user.getCurrentPageState());
        personalNoteRepository.save(personalNote);

        return personalNote;
    }

    /**
     * Returns all user personal notes
     *
     * @param user
     * @return personal notes list
     * @throws PageStateNotFoundException
     */
    public List<PersonalNote> getPersonalNotes(User user) throws PageStateNotFoundException {
        if (user.getCurrentPageState() == null) {
            throw new PageStateNotFoundException(messages.get("pageStateNotFound"));
        }

        return personalNoteRepository.findByPageStateIdOrderByCreatedAt(user.getCurrentPageState().getId());
    }

    /**
     * Updates personal note
     *
     * @param id
     * @param markdownContent
     * @return personalNote
     */
    public PersonalNote updatePersonalNote(String id, String markdownContent) {
        PersonalNote personalNote = personalNoteRepository.findById(id).orElseThrow();
        personalNote.setMarkdownContent(markdownContent);
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
