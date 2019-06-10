package ch.uzh.marugoto.core.data.entity.application;

import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.security.ModelGate;

@Component
public class GameStateGate implements ModelGate {

    @Override
    public boolean canCreate(User user) {
        return true;
    }

    @Override
    public boolean canRead(User user, Object objectModel) {
        GameState gameState = (GameState) objectModel;
        return gameState.getUser().equals(user);
    }

    @Override
    public boolean canUpdate(User user, Object objectModel) {
        return true;
    }

    @Override
    public boolean canDelete(User user, Object objectModel) {
        return true;
    }
}
