package ch.uzh.marugoto.backend.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.application.RequestAction;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.PersonalNoteRequest;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.security.NotebookEntryGate;
import ch.uzh.marugoto.core.service.GeneratePdfService;
import ch.uzh.marugoto.core.service.NotebookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController()
@RequestMapping("/api/notebook")
public class NotebookController extends BaseController {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private GeneratePdfService generatePdfService;
    @Autowired
    private NotebookEntryGate notebookEntryGate;

    @ApiOperation(value = "List all notebook entries", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping("/list")
    public List<NotebookEntryState> getNotebookEntries() throws AuthenticationException {
        return notebookService.getUserNotebookEntryStates(getAuthenticatedUser());
    }

    @ApiOperation(value = "Create personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/{notebookEntryStateId}/personalNote", method = RequestMethod.POST)
    public PersonalNote createPersonalNote(@PathVariable String notebookEntryStateId, @ApiParam ("markdown content") @RequestBody(required = false) PersonalNoteRequest personalNoteRequest) {
        return notebookService.createPersonalNote("notebookEntryState/".concat(notebookEntryStateId), personalNoteRequest.getMarkdownContent());
    }

    @ApiOperation(value="Update personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/{notebookContentId}", method = RequestMethod.PUT)
    public PersonalNote updatePersonalNote(@PathVariable String notebookContentId, @ApiParam ("markdown content") @RequestBody(required = false) PersonalNoteRequest personalNoteRequest) throws AuthenticationException {
        isUserAuthorized(RequestAction.UPDATE, getAuthenticatedUser(), notebookEntryGate, "notebookContent/".concat(notebookContentId));
        return notebookService.updatePersonalNote("notebookContent/".concat(notebookContentId), personalNoteRequest.getMarkdownContent());
    }
    
    @ApiOperation(value = "Downloads notebook entry pdf for current gameState", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping(value = "/pdf/current")
    public ResponseEntity<InputStreamResource> generateCurrentPdf() throws AuthenticationException, CreatePdfException {
        User user = getAuthenticatedUser();
    	List<NotebookEntryState> notebookEntries = notebookService.getUserNotebookEntryStates(user);
    	InputStreamResource inputStreamResource = new InputStreamResource(generatePdfService.createPdf(notebookEntries));

    	return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + user.getName() + ".pdf")
                .body(inputStreamResource);
    }
}
