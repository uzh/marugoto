package ch.uzh.marugoto.core.service;

import java.util.ArrayList;
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
	@Autowired
	private GameMailService gameMailService;

	/**
	 * Returns all notebook entry states for user
	 *
	 * @param user
	 * @return notebookEntries list
	 */
	public List<NotebookEntryState> getUserNotebookEntryStates(User user) {
		List<NotebookEntryState> notebookEntryStateList = notebookEntryStateRepository.findUserNotebookEntryStates(user.getCurrentGameState().getId());
		for (NotebookEntryState notebookEntryState : notebookEntryStateList) {
			List<NotebookContent> oldNotebookContentList = notebookEntryState.getNotebookContent();
			List<NotebookContent> newNotebookContentList = new ArrayList<NotebookContent>();
			for (NotebookContent notebookContent : oldNotebookContentList) {
				if (notebookContent.getExerciseState() == null && notebookContent.getMailState() == null) {
					newNotebookContentList.add(notebookContent);
				}
				if (notebookContent.getExerciseState() != null && notebookContent.getExerciseState().getInputState() != null
					&& notebookContent.getExerciseState().getInputState().compareTo("") != 0) {
					newNotebookContentList.add(notebookContent);
				}
				if (notebookContent.getMailState() != null && notebookContent.getMailState().getMailReplyList().size() > 0) {
					newNotebookContentList.add(notebookContent);
				}
			}
			notebookEntryState.setNotebookContent(newNotebookContentList);
		}
		return notebookEntryStateList;
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
		NotebookEntryState notebookEntryState = notebookEntryStateRepository
				.findLastNotebookEntryState(user.getCurrentGameState().getId());

		createComponentNotebookContent(user, notebookEntryState, notebookContentCreateAt);
		// only on page exit, mail notebook content is created
		if (notebookContentCreateAt.equals(NotebookContentCreateAt.pageExit)) {
			createMailNotebookContent(user, notebookEntryState);
		}
	}

	/**
	 * Creates page components content in notebook
	 *
	 * @param user
	 * @param notebookEntryState
	 * @param notebookContentCreateAt
	 */
	private void createComponentNotebookContent(User user, NotebookEntryState notebookEntryState,
			NotebookContentCreateAt notebookContentCreateAt) {
		Page currentPage = user.getCurrentPageState().getPage();
		for (Component component : componentRepository.findPageComponents(currentPage.getId())) {
			if (component.isShownInNotebook() && component.getShowInNotebookAt() == notebookContentCreateAt) {
				NotebookContent notebookContent = new NotebookContent(component);
				if (component instanceof Exercise) {
					createExerciseNotebookContent(user, (Exercise) component, notebookContent);
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
	public void createExerciseNotebookContent(User user, Exercise exercise, NotebookContent notebookContent) {
		ExerciseState exerciseState = exerciseStateRepository
				.findUserExerciseState(user.getCurrentPageState().getId(), exercise.getId()).orElseThrow();
		notebookContent.setExerciseState(exerciseState);
		notebookContent.setDescription(exercise.getDescriptionForNotebook());
	}

	/**
	 * Create mail notebook content
	 *
	 * @param user
	 */
	public void createMailNotebookContent(User user, NotebookEntryState notebookEntryState) {
		Page currentPage = user.getCurrentPageState().getPage();

		for (MailState mailState : gameMailService.getReceivedMailsForPage(user, currentPage)) {
			if (mailState != null && mailState.getMail().isShownInNotebook()) {
				createNotebookContent(notebookEntryState, new NotebookContent(mailState));
			}
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
