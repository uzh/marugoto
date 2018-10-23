package ch.uzh.marugoto.backend.test.controller;

import static javax.management.Query.value;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.backend.resource.CreatePersonalNote;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.exception.PageStateNotFoundException;
import ch.uzh.marugoto.core.service.NotebookService;
import ch.uzh.marugoto.core.service.PageService;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageService pageService;

    @Autowired
    private NotebookService notebookService;

    @Test()
    public void testCreatePersonalWhenPageStateNotExisting() throws Exception {
        user = new User(UserType.Guest, Salutation.Mr, "test", "tester", "tester@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
        user.setCurrentPageState(null);
        userRepository.save(user);

        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                    post("/api/notebook/personalNote")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> jsonPath("$.exception", value(String.valueOf(PageStateNotFoundException.class))));
    }

    @Test()
    public void testCreatePersonalNote() throws Exception {
        var personalNoteResource = new CreatePersonalNote();
        personalNoteResource.setText("Personal note test");
        mvc.perform(authenticate(
                post("/api/notebook/personalNote")
                        .content(new ObjectMapper().writeValueAsString(personalNoteResource))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPersonalNote() throws Exception {
        pageService.getPageState(pageRepository.findByTitle("Page 1"), user);

        var personalNote = notebookService.createPersonalNote("test note", user);

        mvc.perform(authenticate(
                get("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isOk())
                .andExpect(result -> jsonPath("$.id", value(personalNote.getId())));
    }

    @Test
    public void testDeletePersonalNote() throws Exception {
        pageService.getPageState(pageRepository.findByTitle("Page 1"), user);
        var personalNote = notebookService.createPersonalNote("test note", user);

        mvc.perform(authenticate(
                delete("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isNoContent());
    }
}
