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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.service.GeneratePdfService;
import ch.uzh.marugoto.core.service.NotebookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController()
@RequestMapping("/api/notebook")
public class NotebookController extends BaseController {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private GeneratePdfService generatePdfService;

    @ApiOperation(value = "List all notebook entries", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping("/list")
    public List<NotebookEntryState> getNotebookEntries() throws AuthenticationException {
        return notebookService.getUserNotebookEntryStates(getAuthenticatedUser());
    }

    @ApiOperation(value = "Create personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/{notebookEntryStateId}/personalNote", method = RequestMethod.POST)
    public PersonalNote createPersonalNote(@PathVariable String notebookEntryStateId, @RequestParam String markdownContent) {
        return notebookService.createPersonalNote("notebookEntryState/".concat(notebookEntryStateId), markdownContent);
    }

    @ApiOperation(value="Update personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/{notebookContentId}", method = RequestMethod.PUT)
    public PersonalNote updatePersonalNote(@PathVariable String notebookContentId, @RequestParam String markdownContent) {
        return notebookService.updatePersonalNote("notebookContent/".concat(notebookContentId), markdownContent);
    }
    
    @ApiOperation(value = "Downloads notebook entry pdf", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping(value = "/get/pdf",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> generatePdf () throws AuthenticationException, CreatePdfException {

    	List<NotebookEntryState> notebookEntries = notebookService.getUserNotebookEntryStates(getAuthenticatedUser());
    	ByteArrayInputStream bis = generatePdfService.createPdf(notebookEntries);

    	return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Notebook.pdf")
                .body(new InputStreamResource(bis)); 
    }
}
