package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.topic.Salutation;
import ch.uzh.marugoto.core.data.entity.topic.UserType;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.service.NotebookService;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

    @Autowired
    private NotebookService notebookService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private PersonalNoteRepository personalNoteRepository;

    @Test
    public void testCreatePersonalWhenPageStateNotExisting() throws Exception {
        user = new User(UserType.Guest, Salutation.Mr, "test", "tester", "tester@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
        var page = pageRepository.findByTitle("Page 1");
        user.setCurrentPageState(null);
        userRepository.save(user);
        var notebookEntry = notebookService.getNotebookEntry(page, NotebookEntryAddToPageStateAt.enter).orElse(null);
        var entryId = notebookEntry.getId().replaceAll("[^0-9]","");
        mvc.perform(authenticate(
                    post("/api/notebook/"+ entryId + "/personalNote")
                        .param("markdownContent", "Personal Note Text")))
        		.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.exception", is("PageStateNotFoundException")));
    }

    @Test
    public void testCreatePersonalNote() throws Exception {
      var markdownContent = "New personal note created";
      var page = pageRepository.findByTitle("Page 1");
      var notebookEntry = notebookService.getNotebookEntry(page, NotebookEntryAddToPageStateAt.enter).orElse(null);
      var entryId = notebookEntry.getId().replaceAll("[^0-9]","");
    	mvc.perform(authenticate(
                post("/api/notebook/"+ entryId + "/personalNote")
                        .param("markdownContent", markdownContent)))
    			.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.markdownContent", is(markdownContent)));
    }

    @Test
    public void testGetPersonalNotes() throws Exception {
        var pageState = user.getCurrentPageState();
    	var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryAddToPageStateAt.enter).orElse(null);
    	var entryId = notebookEntry.getId().replaceAll("[^0-9]","");
        mvc.perform(authenticate(get("/api/notebook/"+ entryId +"/personalNote/list")))
        	.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].markdownContent", is("Personal Note Text")));
    }
    
    @Test
    public void testGetNotebookEntries() throws Exception {
    	mvc.perform(authenticate(get("/api/notebook/list")))
    		.andDo(print())
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$[0].title", is("Page 1 entry")))
    		.andExpect(jsonPath("$[1].title", is("Page 1 exit entry")));
    }
    
    @Test
    public void testUpdatePersonalNote() throws Exception {
    	var personalNote = personalNoteRepository.findAll().iterator().next();
        var noteId = personalNote.getId().replaceAll("[^0-9]","");
        var personalNoteText = "Updated personal text";
    	
        mvc.perform(authenticate(
        		put("/api/notebook/personalNote/" + noteId).param("markdownContent", personalNoteText)))
        	.andDo(print())
    		.andExpect(status().isOk())
        	.andExpect(jsonPath("$.markdownContent", is(personalNoteText)));
    }

    @Test
    public void testDeletePersonalNote() throws Exception {
    	var pageState = user.getCurrentPageState();
    	var notebookEntry = notebookService.getNotebookEntry(pageState.getPage(), NotebookEntryAddToPageStateAt.enter).orElse(null);
        var personalNote = notebookService.createPersonalNote(notebookEntry.getId(), "new note", user);

        mvc.perform(authenticate(
                delete("/api/notebook/" + personalNote.getId())))
                .andExpect(status().isNoContent());
    }
}