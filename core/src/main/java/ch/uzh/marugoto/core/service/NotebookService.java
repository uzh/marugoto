package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.NotebookContentCreateAt;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookContentRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.exception.CreateZipException;

@Service
public class NotebookService {

    @Autowired
    private NotebookEntryRepository notebookEntryRepository;
    @Autowired
    private NotebookEntryStateRepository notebookEntryStateRepository;
    @Autowired
    private NotebookContentRepository notebookContentRepository;
    @Autowired
    private PageStateRepository pageStateRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private ExerciseStateRepository exerciseStateRepository;
    @Autowired
    private GeneratePdfService generatePdfService;
    @Autowired
    private FileService fileService;

    /**
     * Returns all notebook entries for user
     *
     * @param user
     * @return notebookEntries list
     */
    public List<NotebookEntryState>getUserNotebookEntries(User user) {
        return notebookEntryStateRepository.findUserNotebookEntries(user.getCurrentGameState().getId());
    }
    
    /**
     * Finds notebook entry by page
     *
     * @param page
     * @return notebookEntry
     */
    public Optional<NotebookEntry> getNotebookEntry(Page page) {
        return notebookEntryRepository.findNotebookEntryByPage(page.getId());
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


    public void initializeStateForNewPage(User user) {
        Page currentPage = user.getCurrentPageState().getPage();
        NotebookEntry notebookEntry = getNotebookEntry(currentPage).orElse(null);

        if (notebookEntry != null) {
            notebookEntryStateRepository.save(new NotebookEntryState(user.getCurrentGameState(), notebookEntry));
        }

        addNotebookContentForPage(user, NotebookContentCreateAt.pageEnter);
    }

    /**
     * Creates NotebookContent for the last NotebookEntryState
     * 
     * @param user
     * @param notebookContentCreateAt pageEnter/pageExit
     */
    public void addNotebookContentForPage(User user, NotebookContentCreateAt notebookContentCreateAt) {
        Page currentPage = user.getCurrentPageState().getPage();
        NotebookEntryState notebookEntryState = notebookEntryStateRepository.findLastNotebookEntryState(user.getCurrentGameState().getId());

        for (Component component : componentRepository.findPageComponents(currentPage.getId())) {
            if (component.isShownInNotebook() && component.getShowInNotebookAt() == notebookContentCreateAt) {
                NotebookContent notebookContent;

                if (component instanceof Exercise) {
                    notebookContent = createExerciseNotebookContent(user, (Exercise) component);
                } else {
                    notebookContent = new NotebookContent(component);
                }

                saveNotebookContent(notebookEntryState, notebookContent);
            }
        }
    }

    /**
     * Add notebook content for exercise
     *
     * @param user
     * @param exercise
     */
    private NotebookContent createExerciseNotebookContent(User user, Exercise exercise) {
        ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(user.getCurrentPageState().getId(), exercise.getId()).orElseThrow();
        NotebookContent notebookContent = new NotebookContent();
        notebookContent.setExerciseState(exerciseState);
        notebookContent.setDescription(exercise.getDescriptionForNotebook());
        return notebookContent;
    }

    /**
     * Save notebookContent to database and add it to NotebookEntryState
     *
     * @param notebookEntryState
     * @param notebookContent
     */
    private void saveNotebookContent(NotebookEntryState notebookEntryState, NotebookContent notebookContent) {
        notebookEntryState.addNotebookContent(notebookContentRepository.save(notebookContent));
        notebookEntryStateRepository.save(notebookEntryState);
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
     * @param notebookEntryStateId
     * @param markdownContent
     * @return personalNote
     */
    public PersonalNote createPersonalNote(String notebookEntryStateId, String markdownContent) {
        NotebookEntryState notebookEntryState = notebookEntryStateRepository.findNotebookEntryStateById(notebookEntryStateId).orElseThrow();

        PersonalNote personalNote = new PersonalNote(markdownContent);
        saveNotebookContent(notebookEntryState, new NotebookContent(personalNote));

        return personalNote;
    }

    /**
     * Updates personal note
     *
     * @param notebookContentId
     * @param markdownContent
     * @return personalNote
     */
    public PersonalNote updatePersonalNote(String notebookContentId, String markdownContent) {
        NotebookContent notebookContent = notebookContentRepository.findById(notebookContentId).orElseThrow();

        PersonalNote personalNote = notebookContent.getPersonalNote();
        personalNote.setMarkdownContent(markdownContent);

        notebookContent.setPersonalNote(personalNote);
        notebookContentRepository.save(notebookContent);

        return personalNote;
    }

    /**
     * Strikethrough personal note
     *
     * @param notebookContentId
     */
    public PersonalNote deletePersonalNote(String notebookContentId) {
        NotebookContent notebookContent = notebookContentRepository.findById(notebookContentId).orElseThrow();
        PersonalNote personalNote = notebookContent.getPersonalNote();
        personalNote.setMarkdownContent("<div class='deleted'>".concat(personalNote.getMarkdownContent().concat("</div>")));
        notebookContentRepository.save(notebookContent);

        return personalNote;
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
            List<NotebookEntryState> notebookEntryList = getUserNotebookEntries(user);
            var notebookName = user.getName().toLowerCase();
            notebooksInputStream.put(notebookName, generatePdfService.createPdf(notebookEntryList));
        }

        return fileService.zipMultipleInputStreams(notebooksInputStream, classId);
    }
}
