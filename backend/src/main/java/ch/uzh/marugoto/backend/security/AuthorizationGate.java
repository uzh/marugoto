package ch.uzh.marugoto.backend.security;

import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.entity.application.RequestAction;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.security.ModelGate;

@Component
public class AuthorizationGate {

    public boolean isUserAuthorized(RequestAction actionName, User user, ModelGate modelGate, Object objectModel) {
        boolean authorized = false;

        switch (actionName) {
            case CREATE:
                authorized = modelGate.canCreate(user);
                break;
            case READ:
                authorized = modelGate.canRead(user, objectModel);
                break;
            case UPDATE:
                authorized = modelGate.canUpdate(user, objectModel);
                break;
            case DELETE:
                authorized = modelGate.canDelete(user, objectModel);
                break;
            default:
                authorized = false;
        }

        return authorized;
    }
}
