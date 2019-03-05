package ch.uzh.marugoto.core.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;
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
    private GeneratePdfService generatePdfService;

    @Autowired
    private FileService fileService;

    @Autowired
    private Messages messages;

    /**
     * Returns all notebook entries for user
     *
     * @param user
     * @return notebookEntries list
     */
    public List<NotebookEntry>getUserNotebookEntries(User user) {
    	return notebookEntryRepository.findUserNotebookEntries(user.getCurrentGameState().getId());
    }
    
    /**
     * Returns all notebook entries with its personal notes
     *
     * @param user
     * @return notebookEntries list
     */
    public List<NotebookEntry>getUserNotebookEntriesWithPersonalNotes(User user) {
    	List<NotebookEntry> notebookEntries = notebookEntryRepository.findUserNotebookEntries(user.getCurrentGameState().getId()).stream()
                .peek(notebookEntry -> notebookEntry.setPersonalNotes(getPersonalNotes(notebookEntry.getId(),user)))
                .collect(Collectors.toList());
    	return notebookEntries;
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
        return save(new PersonalNote(markdownContent, user.getCurrentPageState(), notebookEntry));
    }

    /**
     * Returns all user personal notes
     *
     * @param notebookEntryId
     * @param user
     * @return personal notes list
     */
    public List<PersonalNote> getPersonalNotes(String notebookEntryId, User user) {
        return personalNoteRepository.findAllPersonalNotes(notebookEntryId, user.getCurrentPageState().getId());
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
        return save(personalNote);
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

    /**
     * Saves personal note
     *
     * @param personalNote
     * @return
     */
    private PersonalNote save(PersonalNote personalNote) {
        return personalNoteRepository.save(personalNote);
    }

    /**
     *
     * @param students
     * @param classId
     * @return zipped users notebook pdf
     * @throws CreateZipException
     * @throws CreatePdfException
     */
    public FileInputStream getClassroomNotebooks(List<User> students, String classId) throws CreatePdfException, CreateZipException {
        HashMap<String, InputStream> notebooksInputStream = new HashMap<>();

        for (User user : students) {
            List<NotebookEntry> notebookEntryList = getUserNotebookEntriesWithPersonalNotes(user);
            var notebookName = user.getName().toLowerCase();
            notebooksInputStream.put(notebookName, generatePdfService.createPdf(notebookEntryList));
        }

        return fileService.zipMultipleInputStreams(notebooksInputStream, classId);
    }
}
