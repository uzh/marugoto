package ch.uzh.marugoto.backend.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.uzh.marugoto.backend.controller.UploadService;
import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.service.ExerciseStateService;

@AutoConfigureMockMvc
public class UploadControllerTest extends BaseControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private UploadService uploadService;

	@Test
	public void testGetFileById() throws Exception {
		var exerciseState = exerciseStateService.findUserExerciseStates(user.getId()).get(0);
		var exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
		InputStream inputStream = new URL("https://picsum.photos/600/?random").openStream();
		MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/jpeg", inputStream);
		uploadService.uploadFile(file, exerciseStateId);
		
		mvc.perform(authenticate(get("/api/uploads/" + exerciseStateId)))
				.andDo(print())
				.andExpect(status().isOk());

		uploadService.deleteFile(exerciseStateId);
	}

	@Test
	public void testUploadFile() throws Exception {
		InputStream inputStream = new URL("https://picsum.photos/600/?random").openStream();
		MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/jpeg", inputStream);
		var exerciseState = exerciseStateService.findUserExerciseStates(user.getId()).get(0);
		var exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
		
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mvc.perform(MockMvcRequestBuilders.multipart("/api/uploads")
				.file(file)
				.param("exerciseStateId", exerciseStateId))		
			.andDo(print())
			.andExpect(status().isOk());
	}
	@Test
	public void testDeleteFile() throws Exception {
		var exerciseState = exerciseStateService.findUserExerciseStates(user.getId()).get(0);
		var exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
		InputStream inputStream = new URL("https://picsum.photos/600/?random").openStream();
		MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/jpeg", inputStream);
		uploadService.uploadFile(file, exerciseStateId);
		
		mvc.perform(authenticate(delete("/api/uploads/" + exerciseStateId)))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
}
