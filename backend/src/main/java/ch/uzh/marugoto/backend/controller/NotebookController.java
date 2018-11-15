package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.resource.CreatePersonalNote;
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
    @RequestMapping(value = "/personalNote", method = RequestMethod.POST)
    public ResponseEntity<PersonalNote> createPersonalNote(@RequestBody @Validated CreatePersonalNote createPersonalNote) throws AuthenticationException, PageStateNotFoundException {
        PersonalNote personalNote = notebookService.createPersonalNote(createPersonalNote.getText(), getAuthenticatedUser());
        return ResponseEntity.ok(personalNote);
    }

    @ApiOperation(value = "Finds personal note by ID", authorizations = { @Authorization(value = "apiKey") })
    @GetMapping("/personalNote/{id}")
    public ResponseEntity<PersonalNote> getPersonalNote(@PathVariable String id) {
        PersonalNote personalNote = notebookService.getPersonalNote("personalNote/" + id);
        return ResponseEntity.ok(personalNote);
    }

    @ApiOperation(value="Update personal note", authorizations = { @Authorization(value = "apiKey") })
    @RequestMapping(value = "/personalNote/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PersonalNote> updatePersonalNote(@PathVariable String id, @RequestParam String text) {
        PersonalNote personalNote = notebookService.updatePersonalNote("personalNote/" + id, text);
        return ResponseEntity.ok(personalNote);
    }

    @ApiOperation(value="Delete personal note", authorizations = { @Authorization(value="apiKey")})
    @RequestMapping(value = "/personalNote/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PersonalNote> deletePersonalNote(@PathVariable String id) {
        notebookService.deletePersonalNote("personalNote/" + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
