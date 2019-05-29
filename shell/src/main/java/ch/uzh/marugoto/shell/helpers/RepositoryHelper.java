package ch.uzh.marugoto.shell.helpers;

import java.util.List;

import org.springframework.data.repository.support.Repositories;

import com.arangodb.springframework.repository.ArangoRepository;

import ch.uzh.marugoto.core.data.entity.state.DialogState;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.DialogStateRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryStateRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class RepositoryHelper {

    @SuppressWarnings({ "rawtypes" })
	public static ArangoRepository getRepository(Class<?> clazz) {
        ArangoRepository repository;
        String[] componentsName = new String[] { "Exercise", "Component" };
        String[] notificationsName = new String[] { "Mail", "Dialog" };
        String resourcesName = "Resource";
        String entryName = "NotebookEntry";

        repository = (ArangoRepository) new Repositories(BeanUtil.getContext()).getRepositoryFor(clazz).orElse(null);

        if (repository == null) {
            if (StringHelper.stringContains(clazz.getName(), componentsName)) {
                repository = BeanUtil.getBean(ComponentRepository.class);
            } else if (StringHelper.stringContains(clazz.getName(), notificationsName)) {
                repository = BeanUtil.getBean(NotificationRepository.class);
            } else if (clazz.getName().contains(resourcesName)) {
                repository = BeanUtil.getBean(ResourceRepository.class);
            } else if (clazz.getName().contains(entryName)) {
                repository = BeanUtil.getBean(NotebookEntryRepository.class);
            }
        }

        if (repository == null) {
            throw new RuntimeException("Repository not found!");
        }

        return repository;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void delete(Object obj) {
        ArangoRepository rep = getRepository(obj.getClass());
        rep.delete(obj);
    }

    public static List<GameState> getGameStatesForTopic(String topicId) {
        GameStateRepository gameStateRepository = (GameStateRepository) getRepository(GameState.class);
        return gameStateRepository.findByTopicId(topicId);
    }

    public static void deleteUserStates(String gameStateId) {
        UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
        userRepository.unsetUserStates(gameStateId);
    }

    public static void deletePageStates(String gameStateId) {
        PageStateRepository pageStateRepository = BeanUtil.getBean(PageStateRepository.class);
        var pageStates = pageStateRepository.findUserPageStates(gameStateId);
        for (PageState pageState : pageStates) {
            pageStateRepository.delete(pageState);
        }
    }

    public static void deleteExerciseStates(String pageStateId) {
        ExerciseStateRepository exerciseStateRepository = BeanUtil.getBean(ExerciseStateRepository.class);
        List<ExerciseState> exerciseStates = exerciseStateRepository.findByPageStateId(pageStateId);
        for (ExerciseState exerciseState : exerciseStates) {
            exerciseStateRepository.delete(exerciseState);
        }
    }

    public static void deleteDialogStates(String gameStateId) {
        DialogStateRepository dialogStateRepository = BeanUtil.getBean(DialogStateRepository.class);
        List<DialogState> dialogStates = dialogStateRepository.findByGameState(gameStateId);
        for (DialogState dialogState : dialogStates) {
            dialogStateRepository.delete(dialogState);
        }
    }

    public static void deleteMailStates(String gameStateId) {
        MailStateRepository mailStateRepository = BeanUtil.getBean(MailStateRepository.class);
        List<MailState> mailStates = mailStateRepository.findAllForGameState(gameStateId);
        for (MailState mailState : mailStates) {
            mailStateRepository.delete(mailState);
        }
    }

    public static void deleteNotebookEntryStates(String gameStateId) {
        NotebookEntryStateRepository notebookEntryStateRepository = BeanUtil.getBean(NotebookEntryStateRepository.class);
        List<NotebookEntryState> notebookEntryStates = notebookEntryStateRepository.findUserNotebookEntryStates(gameStateId);
        for (NotebookEntryState notebookEntryState : notebookEntryStates) {
            notebookEntryStateRepository.delete(notebookEntryState);
        }
    }


    public static List<PageState> findPageStatesByGameState(String gameStateId) {
        PageStateRepository pageStateRepository = BeanUtil.getBean(PageStateRepository.class);
        return pageStateRepository.findUserPageStates(gameStateId);
    }
}
