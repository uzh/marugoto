package ch.uzh.marugoto.backend.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.UploadExerciseService;

@AutoConfigureMockMvc
public class UploadControllerTest extends BaseControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	private MockMultipartFile file;
	private String exerciseStateId;
	
	@Before
	public void init() throws IOException {
		super.before();
		ExerciseState exerciseState = exerciseStateService.findUserExerciseStates(user.getId()).get(0);
		InputStream inputStream = new URL("https://picsum.photos/600/?random").openStream();
		file = new MockMultipartFile("file", "image.png", "image/jpeg", inputStream);
		exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
	}

	@Test
	public void testGetFileById() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		mvc.perform(authenticate(get("/api/uploads/" + exerciseStateId)))
				.andDo(print())
				.andExpect(status().isOk());

		uploadExerciseService.deleteFile(exerciseStateId);
	}

	@Test
	public void testUploadFile() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mvc.perform(MockMvcRequestBuilders.multipart("/api/uploads")
				.file(file)
				.param("exerciseStateId", exerciseStateId))		
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testDeleteFile() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		
		mvc.perform(authenticate(delete("/api/uploads/" + exerciseStateId)))
			.andDo(print())
			.andExpect(status().isOk());
	}
}
