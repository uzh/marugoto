package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.DialogService;
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

    @ApiOperation(value = "Get next dialog speech", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("dialog/dialogResponse/{dialogResponseId}")
    public HashMap<String, Object> dialogResponse(@ApiParam("Dialog response ID") @PathVariable String dialogResponseId) throws AuthenticationException, PageTransitionNotAllowedException {
        User user = getAuthenticatedUser();
        DialogResponse dialogResponse = dialogService.dialogResponseChosen(dialogResponseId, user);

        var response = new HashMap<String, Object>();
        response.put("stateChanged", false);

        if (dialogResponse.getPageTransition() != null) {
            stateService.doPageTransition(TransitionChosenOptions.player, dialogResponse.getPageTransition().getId(), user);
            response.replace("stateChanged", true);
        } else {
            response.put("speech", dialogResponse.getTo());
            response.put("answers", dialogService.getResponsesForDialogSpeech(dialogResponse.getTo()));
        }

        return response;
    }
}
