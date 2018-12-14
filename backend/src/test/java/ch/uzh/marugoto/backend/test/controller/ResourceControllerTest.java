package ch.uzh.marugoto.backend.test.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ResourceControllerTest extends BaseControllerTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void testFindResource() throws Exception {
        Resource resource = resourceRepository.findAll().iterator().next();

        mvc.perform(authenticate(get("/api/resources/" + resource.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is("/dummy/path")));
    }
}
