package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.Component;
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

@Service
public class NotebookService {

	@Autowired
	private NotebookEntryRepository notebookEntryRepository;
	@Autowired
	private NotebookEntryStateRepository notebookEntryStateRepository;
	@Autowired
	private NotebookContentRepository notebookContentRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;

	
	/**
	 * Returns all notebook entry states for user
	 *
	 * @param user
	 * @return notebookEntries list
	 */
	public List<NotebookEntryState> getUserNotebookEntryStates(User user) {
		return notebookEntryStateRepository.findUserNotebookEntryStates(user.getCurrentGameState().getId());
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
		NotebookEntryState notebookEntryState = notebookEntryStateRepository
				.findLastNotebookEntryState(user.getCurrentGameState().getId());

		for (Component component : componentRepository.findPageComponents(currentPage.getId())) {
			if (component.isShownInNotebook() && component.getShowInNotebookAt() == notebookContentCreateAt) {
				NotebookContent notebookContent = new NotebookContent(component);
				if (component instanceof Exercise) {
					createExerciseNotebookContent(user, (Exercise)component, notebookContent);
				}
				createNotebookContent(notebookEntryState, notebookContent);
			}
		}
	}

	/**
	 * Add notebook content for exercise
	 *
	 * @param user
	 * @param exercise
	 */
	public NotebookContent createExerciseNotebookContent(User user, Exercise exercise, NotebookContent notebookContent) {
		ExerciseState exerciseState = exerciseStateRepository.findUserExerciseState(user.getCurrentPageState().getId(), exercise.getId()).orElseThrow();
		notebookContent.setExerciseState(exerciseState);
		notebookContent.setDescription(exercise.getDescriptionForNotebook());
		return notebookContent;
	}

	/**
	 * Create mail notebook content
	 *
	 * @param mailState
	 */
	public void createMailNotebookContent(MailState mailState) {
		if (getNotebookEntryForMail(mailState.getMail()).isPresent()) {
			NotebookEntryState notebookEntryState = notebookEntryStateRepository
					.findLastNotebookEntryState(mailState.getGameState().getId());
			createNotebookContent(notebookEntryState, new NotebookContent(mailState));
		}
	}

	/**
	 * Save notebookContent to database and add it to NotebookEntryState
	 *
	 * @param notebookEntryState
	 * @param notebookContent
	 */
	private void createNotebookContent(NotebookEntryState notebookEntryState, NotebookContent notebookContent) {
		notebookEntryState.addNotebookContent(notebookContentRepository.save(notebookContent));
		notebookEntryStateRepository.save(notebookEntryState);
	}

	/**
	 * Creates user personal note
	 *
	 * @param notebookEntryStateId
	 * @param markdownContent
	 * @return personalNote
	 */
	public PersonalNote createPersonalNote(String notebookEntryStateId, String markdownContent) {
		NotebookEntryState notebookEntryState = notebookEntryStateRepository
				.findNotebookEntryStateById(notebookEntryStateId).orElseThrow();

		PersonalNote personalNote = new PersonalNote(markdownContent);
		createNotebookContent(notebookEntryState, new NotebookContent(personalNote));

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
}
