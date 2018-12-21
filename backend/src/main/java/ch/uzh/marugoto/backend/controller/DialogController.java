package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.service.DialogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class DialogController extends BaseController {

    @Autowired
    private DialogService dialogService;

    @ApiOperation(value = "Get dialog speech", authorizations = { @Authorization(value = "apiKey")})
    @GetMapping("dialog/dialogResponse/{dialogResponseId}")
    public HashMap<String, Object> dialogResponse(@ApiParam("Dialog response ID") @PathVariable String dialogResponseId) {
        DialogResponse dialogResponse = dialogService.getResponseById(dialogResponseId);

        var response = new HashMap<String, Object>();

        if (dialogResponse.getPageTransition() != null) {
            // TODO
            // maybe we should add redirect here
            response.put("useTransition", "page/doTransition/" + dialogResponse.getPageTransition().getId());
        } else {
            response.put("speech", dialogResponse.getTo().getMarkdownContent());
            response.put("answers", dialogService.getResponseForDialogSpeech(dialogResponse.getTo()));
        }

        return response;
    }
}
