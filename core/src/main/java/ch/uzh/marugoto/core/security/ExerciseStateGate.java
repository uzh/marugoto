package ch.uzh.marugoto.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;

@Component
public class ExerciseStateGate implements ModelGate {

    @Autowired
    private ExerciseStateRepository exerciseStateRepository;

    @Override
    public boolean canCreate(User user) {
        return true;
    }

    @Override
    public boolean canRead(User user, Object objectModel) {
        return isExerciseStateBelongsToUser(user, objectModel);
    }

    @Override
    public boolean canUpdate(User user, Object objectModel) {
        return isExerciseStateBelongsToUser(user, objectModel);
    }

    @Override
    public boolean canDelete(User user, Object objectModel) {
        return isExerciseStateBelongsToUser(user, objectModel);
    }

    private boolean isExerciseStateBelongsToUser(User user, Object objectModel) {
        ExerciseState exerciseState;
        if (objectModel instanceof ExerciseState) {
            exerciseState = (ExerciseState) objectModel;
        } else {
            exerciseState = exerciseStateRepository.findById(objectModel.toString()).orElseThrow();
        }

        return exerciseState.getPageState().getGameState().getUser().equals(user);
    }
}
