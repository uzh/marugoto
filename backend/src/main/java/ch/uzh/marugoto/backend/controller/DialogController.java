package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;
import ch.uzh.marugoto.core.exception.PageTransitionNotAllowedException;
import ch.uzh.marugoto.core.service.DialogService;
import ch.uzh.marugoto.core.service.NotebookService;
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
    private NotebookService notebookService;

    @ApiOperation(value = "Get next dialog speech", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("dialog/dialogResponse/{dialogResponseId}")
    public HashMap<String, Object> dialogResponse(@ApiParam("Dialog response ID") @PathVariable String dialogResponseId) throws AuthenticationException, PageTransitionNotAllowedException {
        DialogResponse dialogResponse = dialogService.getResponseById(dialogResponseId);
        var response = new HashMap<String, Object>();
        var user = getAuthenticatedUser();
        
        if (dialogResponse.getPageTransition() != null) {
            var nextPage = stateService.doPageTransition(TransitionChosenOptions.player, dialogResponse.getPageTransition().getId(), user);
            response = stateService.getStates(user);
            response.put("page", nextPage);
        } else {
            response.put("speech", dialogService.getNextDialogSpeech(dialogResponse));
        }
        notebookService.addNotebookEntryForDialogResponse(user.getCurrentPageState(), dialogResponse);
        return response;
    }
}
