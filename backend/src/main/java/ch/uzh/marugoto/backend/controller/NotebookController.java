package ch.uzh.marugoto.backend.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.PersonalNote;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController()
@RequestMapping("/api/notebook")
public class NotebookController extends BaseController {

    @Autowired
    private NotebookService notebookService;

    @ApiOperation(value = "Create new personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/{notebookEntryId}/personalNote", method = RequestMethod.POST)
    public PersonalNote createPersonalNote(@PathVariable String notebookEntryId, @RequestParam String markdownContent) throws AuthenticationException, PageStateNotFoundException {
        PersonalNote personalNote = notebookService.createPersonalNote(notebookEntryId, markdownContent, getAuthenticatedUser());
        return personalNote;
    }

    @ApiOperation(value = "Finds all personal notes regarding notebookEntry", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping("/{notebookEntryId}/personalNote/list")
    public List<PersonalNote> getPersonalNotes(@PathVariable String notebookEntryId) throws AuthenticationException  {
        return notebookService.getPersonalNotes("notebookEntry/" + notebookEntryId);
    }
    
    @ApiOperation(value = "Finds all assigned notebook entries", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping("/list")
    public List<NotebookEntry> getNotebookEntries() throws AuthenticationException, PageStateNotFoundException {
        return notebookService.getUserNotebookEntries(getAuthenticatedUser());
    }

    @ApiOperation(value="Update personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/personalNote/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PersonalNote> updatePersonalNote(@PathVariable String id, @RequestParam String markdownContent) {
        PersonalNote personalNote = notebookService.updatePersonalNote("personalNote/" + id, markdownContent);
        return ResponseEntity.ok(personalNote);
    }

    @ApiOperation(value="Delete personal note", authorizations = { @Authorization(value="apiKey")})
    @RequestMapping(value = "/personalNote/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PersonalNote> deletePersonalNote(@PathVariable String id) {
        notebookService.deletePersonalNote("personalNote/" + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
