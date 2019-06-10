package ch.uzh.marugoto.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.entity.application.GameStateGate;
import ch.uzh.marugoto.core.data.entity.application.RequestAction;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.exception.ModelGateNotFoundException;
import ch.uzh.marugoto.core.security.ClassroomGate;
import ch.uzh.marugoto.core.security.ExerciseStateGate;
import ch.uzh.marugoto.core.security.ModelGate;
import ch.uzh.marugoto.core.security.NotebookEntryGate;

@Component
public class AuthorizationGate {

    @Autowired
    private ExerciseStateGate exerciseStateGate;
    @Autowired
    private GameStateGate gameStateGate;
    @Autowired
    private ClassroomGate classroomGate;
    @Autowired
    private NotebookEntryGate notebookEntryGate;

    private ModelGate getModelGateInstance(Class modelClass) throws ModelGateNotFoundException {
        ModelGate modelGate;
        switch (modelClass.getSimpleName()) {
            case "ExerciseStateGate":
                modelGate = exerciseStateGate;
                break;
            case "GameStateGate":
                modelGate = gameStateGate;
                break;
            case "ClassroomGate":
                modelGate = classroomGate;
                break;
            case "NotebookEntryGate":
                modelGate = notebookEntryGate;
                break;
            default:
                throw new ModelGateNotFoundException();
        }

        return modelGate;
    }

    public boolean isUserAuthorized(RequestAction actionName, User user, Class modelGateClass, Object objectModel) {
        boolean authorized;

        try {
            ModelGate modelGate = getModelGateInstance(modelGateClass);
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
        } catch (ModelGateNotFoundException e) {
            authorized = false;
        }


        return authorized;
    }
}
