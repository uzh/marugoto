package ch.uzh.marugoto.backend.test.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class DialogControllerTest extends BaseControllerTest {
    @Autowired
    private DialogResponseRepository dialogResponseRepository;

    @Test
    public void testDialogResponse() throws Exception {
        var r1 = new DialogResponse();
        r1.setButtonText("Yes");
        var dialogResponse1 = dialogResponseRepository.findOne(Example.of(r1)).orElse(null);

        assert dialogResponse1 != null;
        mvc.perform(authenticate(get("/api/dialog/"+ dialogResponse1.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.speech.markdownContent").value("Alright, concentrate then!"))
                .andExpect(jsonPath("$.speech.answers", hasSize(1)));

        var r2 = new DialogResponse();
        r2.setButtonText("No");
        var dialogResponse2 = dialogResponseRepository.findOne(Example.of(r2)).orElse(null);

        assert dialogResponse2 != null;
        mvc.perform(authenticate(get("/api/dialog/"+ dialogResponse2.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.speech.markdownContent").value("Then, goodbye!"))
                .andExpect(jsonPath("$.speech.answers", hasSize(0)));

        var r3 = new DialogResponse();
        r3.setButtonText("Continue");
        var dialogResponse3 = dialogResponseRepository.findOne(Example.of(r3)).orElse(null);

        assert dialogResponse3 != null;
        mvc.perform(authenticate(get("/api/dialog/"+ dialogResponse3.getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Page transition is not allowed.")));
    }
}
