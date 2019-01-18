package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.UploadExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class UploadExerciseServiceTest extends BaseCoreTest{

	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
	private UserRepository userRepository;
	
	private ExerciseState exerciseState;
	private InputStream inputStream;
	private MockMultipartFile file;
	private String exerciseStateId;
	
	
	@Before
	public void init() throws MalformedURLException, IOException {
		super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
		exerciseState = exerciseStateService.findUserExerciseStates(user.getId()).get(0);
		inputStream = new URL("https://picsum.photos/600").openStream();
		file = new MockMultipartFile("file", "image.jpg", "image/jpeg", inputStream);
		exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
	}
	
	@Test
	public void testUploadFile() throws ParseException, Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
	}
	
	@Test
	public void testGetFileById() throws ParseException, Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		File file = uploadExerciseService.getFileByExerciseId(exerciseStateId);
		assertNotNull(file);
		uploadExerciseService.deleteFile(exerciseStateId);
	}
	
	@Test
	public void testDeleteFile() throws ParseException, Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		uploadExerciseService.deleteFile(exerciseStateId);
		File file = new File(UploadExerciseService.getUploadDirectory() +"/"+ exerciseState.getInputState());
		assertFalse(file.exists());
	}
}


