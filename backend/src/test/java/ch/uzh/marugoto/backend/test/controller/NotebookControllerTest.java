package ch.uzh.marugoto.backend.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import ch.uzh.marugoto.backend.resource.CreatePersonalNote;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.StateService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private StateService stateService;

    @Test()
    public void testCreatePersonalNote() throws Exception {

        var page = pageRepository.findByTitle("Page 2");
        stateService.getPageState(page, userRepository.findByMail("defaultuser@marugoto.ch"));

        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                post("/api/notebook/note")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
    }

    @Test()
    public void testCreatePersonalNoteThrowsException() throws Exception {
        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                post("/api/notebook/note")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isBadRequest());
    }
}
