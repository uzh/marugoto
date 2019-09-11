package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.service.NotebookService;

@AutoConfigureMockMvc
public class NotebookControllerTest extends BaseControllerTest {

    @Autowired
    private NotebookService notebookService;
    
    @Test
    public void testGetNotebookEntries() throws Exception {
        notebookService.initializeStateForNewPage(user);
    	mvc.perform(authenticate(get("/api/notebook/list")))
    		.andDo(print())
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$[0].title", is("Page 1 entry")));
    }
    
    @Test
    public void testCreatePersonalNote() throws Exception {
      var markdownContent = "New personal note created";
      notebookService.initializeStateForNewPage(user);
      var notebookEntryState = notebookService.getUserNotebookEntryStates(user);
      var entryId = notebookEntryState.get(0).getId().replaceAll("[^0-9]","");
      mvc.perform(authenticate(post("/api/notebook/"+ entryId + "/personalNote")
    		  		.content("{ \"markdownContent\": \"" + markdownContent + "\" }"))
    		  		.contentType(MediaType.APPLICATION_JSON_UTF8))
    				.andExpect(status().isOk());
    }
    
    @Test
    public void testUpdatePersonalNote() throws Exception {
    	var markdownContent = "New personal note created";
    	notebookService.initializeStateForNewPage(user);
        // notebook state before personal note
    	var oldNotebookEntryState = notebookService.getUserNotebookEntryStates(user).get(0);
        notebookService.createPersonalNote(oldNotebookEntryState.getId(), markdownContent);
        // notebook state after created personal note
        var newNotebookEntryState = notebookService.getUserNotebookEntryStates(user).get(0);
        var notebookContent = newNotebookEntryState.getNotebookContent().get(2);
        
        var personalNoteText = "Updated personal note";
        var entryId = notebookContent.getId().replaceAll("[^0-9]","");
        
        mvc.perform(authenticate(put("/api/notebook/" + entryId)
        		.content("{ \"markdownContent\": \"" + personalNoteText + "\" }"))
        		.contentType(MediaType.APPLICATION_JSON_UTF8))
        		.andExpect(status().isOk());
    }
    
    /*
    @Test
    public void testGeneratePdf() throws Exception {
    	notebookService.initializeStateForNewPage(user);
    	mvc.perform(authenticate(get("/api/notebook/pdf/current")))
		.andDo(print())
		.andExpect(status().isOk());
    }*/
}