package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
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
     * Returns all notebook entries with its personal notes
     *
     * @param user
     * @return notebookEntries list
     */
    public List<NotebookEntry>getUserNotebookEntries(User user) {
    	return notebookEntryRepository.findUserNotebookEntries(user.getId()).stream().map(notebookEntry -> {
            notebookEntry.setPersonalNotes(getPersonalNotes(notebookEntry.getId()));
            return notebookEntry;
        }).collect(Collectors.toList());    
    }
    
    /**
     * Finds notebook entry by page
     *
     * @param page
     * @param addToPageStateAt
     * @return notebookEntry
     */
    public Optional<NotebookEntry> getNotebookEntry(Page page, NotebookEntryAddToPageStateAt addToPageStateAt) {
        return notebookEntryRepository.findNotebookEntryByCreationTime(page.getId(), addToPageStateAt);
    }
    
    /**
     * Finds notebookEntry by dialogResponse
     * 
     * @param dialogResponse
     * @return notebookEntry
     */
    public Optional<NotebookEntry> getNotebookEntryForDialogResponse(DialogResponse dialogResponse) {
    	return notebookEntryRepository.findNotebookEntryByDialogResponse(dialogResponse.getId());
    }
    
    /**
     * Finds notebookEntry by mailExercise
     * 
     * @param mail
     * @return notebookEntry
     */
    public Optional<NotebookEntry> getNotebookEntryForMail(Mail mail) {
    	return notebookEntryRepository.findByMailId(mail.getId());
    }
    
    /**
     * @param currentPageState
     * @param addToPageStateAt
     */
    public void addNotebookEntry(PageState currentPageState, NotebookEntryAddToPageStateAt addToPageStateAt) {
    	getNotebookEntry(currentPageState.getPage(), addToPageStateAt).ifPresent(notebookEntry -> {
            currentPageState.addNotebookEntry(notebookEntry);
            pageStateRepository.save(currentPageState);
        });
    }
    
    /**
     * @param currentPageState
     * @param dialogResponse
     */
    public void addNotebookEntryForDialogResponse(PageState currentPageState, DialogResponse dialogResponse) {
    	getNotebookEntryForDialogResponse(dialogResponse).ifPresent(notebookEntry -> {
            currentPageState.addNotebookEntry(notebookEntry);
            pageStateRepository.save(currentPageState);
        });
    }
    
    /**
     * @param currentPageState
     * @param mail
     */
    public void addNotebookEntryForMail(PageState currentPageState, Mail mail) {
    	getNotebookEntryForMail(mail).ifPresent(notebookEntry -> {
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
    public PersonalNote createPersonalNote(String notebookEntryId, String markdownContent, User user) throws PageStateNotFoundException {
        if (user.getCurrentPageState() == null) {
            throw new PageStateNotFoundException(messages.get("pageStateNotFound"));
        }
        
        NotebookEntry notebookEntry = notebookEntryRepository.findById(notebookEntryId).orElseThrow();       
        PersonalNote personalNote = new PersonalNote(markdownContent);
        personalNote.setNotebookEntry(notebookEntry);
        personalNoteRepository.save(personalNote);

        return personalNote;
    }

    /**
     * Returns all user personal notes
     *
     * @param notebookEntryId
     * @return personal notes list
     */
    public List<PersonalNote> getPersonalNotes(String notebookEntryId) {
        return personalNoteRepository.findByNotebookEntryIdOrderByCreatedAt(notebookEntryId);
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
