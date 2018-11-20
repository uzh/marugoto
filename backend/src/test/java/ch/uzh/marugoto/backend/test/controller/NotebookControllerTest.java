package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
import ch.uzh.marugoto.core.service.NotebookService;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

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
                .andExpect(jsonPath("$.exception", is("PageStateNotFoundException")));
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
    public void testGetPersonalNotes() throws Exception {
        notebookService.createPersonalNote("test note", user);
        notebookService.createPersonalNote("test note2", user);

        mvc.perform(authenticate(get("/api/notebook/personalNote/list")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].markdownContent", is("test note")))
            .andExpect(jsonPath("$[1].markdownContent", is("test note2")));
    }

    @Test
    public void testDeletePersonalNote() throws Exception {
        var personalNote = notebookService.createPersonalNote("test note", user);

        mvc.perform(authenticate(
                delete("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isNoContent());
    }
}
