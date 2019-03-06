package ch.uzh.marugoto.backend.test.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.ClassroomMember;
import ch.uzh.marugoto.core.data.repository.ClassroomMemberRepository;
import ch.uzh.marugoto.core.data.repository.ClassroomRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ClassroomControllerTest extends BaseControllerTest {

    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private ClassroomMemberRepository classroomMemberRepository;
    private Classroom classroom;

    public synchronized void before() {
        super.before();
        classroom = new Classroom("Unit test", "unit testing classroom", LocalDate.now(), LocalDate.now().plusDays(10));
        classroomRepository.save(classroom);
        classroomMemberRepository.save(new ClassroomMember(classroom, user));
    }

    @Test
    public void testListClasses() throws Exception {
        mvc.perform(authenticateSupervisor(get("/api/classroom/list")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    public void testViewClass() throws Exception {
        mvc.perform(authenticateSupervisor(get("/api/" + classroom.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(classroom.getId())));
    }

    @Test
    public void testCreateClass() throws Exception {
        var name = "New Class";
        mvc.perform(authenticateSupervisor(post("/api/classroom/new"))
                    .content("{ \"name\": \"" + name + "\", \"startClassAt\": \"02.12.2019\", \"endClassAt\": \"22.12.2019\" }")
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)));
    }

    @Test
    public void testEditClass() throws Exception {
        var editName = "Edit test";

        mvc.perform(authenticateSupervisor(put("/api/" + classroom.getId()))
                .content("{ \"name\": \"" + editName + "\" }")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(editName)));
    }

    @Test
    public void testDownloadNotebooks() throws Exception {
        Iterable<Classroom> classrooms = classroomRepository.findAll();
        assert classrooms.iterator().hasNext();

        var classroom = classrooms.iterator().next();

        mvc.perform(authenticateSupervisor(get("/api/" + classroom.getId() + "/notebooks")))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
