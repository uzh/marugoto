package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.GameStateBrokenException;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.DialogService;
import ch.uzh.marugoto.core.service.PageTransitionStateService;
import ch.uzh.marugoto.core.service.StateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class DialogController extends BaseController {

    @Autowired
    private DialogService dialogService;
    @Autowired
    private StateService stateService;
    @Autowired
    private PageTransitionStateService pageTransitionStateService;

    @ApiOperation(value = "Get next dialog speech", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("dialog/dialogResponse/{dialogResponseId}")
    public HashMap<String, Object> dialogResponse(@ApiParam("Dialog response ID") @PathVariable String dialogResponseId) throws AuthenticationException, PageTransitionNotAllowedException, GameStateBrokenException {
        var response = new HashMap<String, Object>();
        User user = getAuthenticatedUser();
        DialogResponse dialogResponse = dialogService.dialogResponseSelected(dialogResponseId, user);

        switch (dialogService.getDialogAction(dialogResponse)) {
            case transition:
                stateService.doPageTransition(TransitionChosenOptions.player, dialogResponse.getPageTransition().getId(), user);
                response.put("stateChanged", true);
                break;
            case nextDialog:
                response.put("speech", dialogResponse.getTo());
                response.put("answers", dialogService.getResponsesForDialog(dialogResponse.getTo()));
                break;
            case none:
                response.put("stateChanged", pageTransitionStateService.checkPageTransitionStatesAvailability(user));
        }

        return response;
    }
}
