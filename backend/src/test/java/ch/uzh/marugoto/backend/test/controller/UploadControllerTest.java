package ch.uzh.marugoto.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.service.UploadExerciseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UploadControllerTest extends BaseControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	private MockMultipartFile file;
	private String exerciseStateId;
	
	@Before
	public void init() throws IOException {
		super.before();
		var exerciseState = exerciseStateRepository.findByPageStateId(user.getCurrentPageState().getId()).get(0);
		var inputStream = new URL("https://picsum.photos/300").openStream();
		file = new MockMultipartFile("file", "image.jpg", "image/jpg", inputStream);
		exerciseStateId = exerciseState.getId();
	}

	@Test
	public void testGetFileById() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		var url = "/api/uploads/" + exerciseStateId;
		mvc.perform(authenticate(get("/api/uploads/" + exerciseStateId)))
				.andDo(print())
				.andExpect(status().isOk());

//		uploadExerciseService.deleteFile(exerciseStateId);
	}

	@Test
	public void testUploadFile() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mvc.perform(MockMvcRequestBuilders.multipart("/api/uploads")
				.file(file)
				.param("exerciseStateId", exerciseStateId))		
			.andDo(print())
			.andExpect(status().isOk());

        uploadExerciseService.deleteFile(exerciseStateId);
	}

	@Test
	public void testDeleteFile() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		
		mvc.perform(authenticate(delete("/api/uploads/" + exerciseStateId)))
			.andDo(print())
			.andExpect(status().isOk());
	}
}
