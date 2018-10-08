package ch.uzh.marugoto.backend.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import ch.uzh.marugoto.backend.resource.CreatePersonalNote;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.StateService;

import static javax.management.Query.value;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private StateService stateService;

    @Autowired
    private NotebookService notebookService;

    private User user;

    @Before
    public synchronized void before() {
        super.before();
        user = userRepository.findByMail("defaultuser@marugoto.ch");
    }

    @Test()
    public void testCreatePersonalNote() throws Exception {

        var page = pageRepository.findByTitle("Page 2");
        stateService.getPageState(page, user);

        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                post("/api/notebook/personalNote")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
    }

    @Test()
    public void testCreatePersonalWhenPageStateNotExisting() throws Exception {
        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                post("/api/notebook/personalNote")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> jsonPath("$.exception", value(String.valueOf(PageStateNotFoundException.class))));
    }

    @Test
    public void testGetPersonalNote() throws Exception {
        stateService.getPageState(pageRepository.findByTitle("Page 1"), user);

        var personalNote = notebookService.createPersonalNote("test note", user);

        mvc.perform(authenticate(
                get("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isOk())
                .andExpect(result -> jsonPath("$.id", value(personalNote.getId())));
    }

    @Test
    public void testDeletePersonalNote() throws Exception {
        stateService.getPageState(pageRepository.findByTitle("Page 1"), user);
        var personalNote = notebookService.createPersonalNote("test note", user);

        mvc.perform(authenticate(
                delete("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isNoContent());
    }
}
