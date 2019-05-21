package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.ExerciseStateService;
import ch.uzh.marugoto.core.service.PageStateService;
import ch.uzh.marugoto.core.service.UploadExerciseService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class UploadExerciseServiceTest extends BaseCoreTest{

	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private UploadExerciseService uploadExerciseService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private GameStateRepository gameStateRepository;
	
	private ExerciseState exerciseState;
	private InputStream inputStream;
	private MockMultipartFile file;
	private String exerciseStateId;
	
	
	@Before
	public void init() throws IOException {
		super.before();
        var user = userRepository.findByMail("unittest@marugoto.ch");
		exerciseState = exerciseStateRepository.findByPageStateId(user.getCurrentPageState().getId()).get(0);
		inputStream = new URL("https://picsum.photos/600").openStream();
		file = new MockMultipartFile("file", "image.jpg", "image/jpeg", inputStream);
		exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
	}
	
	@Test
	public void testUploadFile() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
	}
	
	@Test
	public void testGetFileById() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		File file = uploadExerciseService.getFileByExerciseId(exerciseStateId);
		assertNotNull(file);
		uploadExerciseService.deleteFile(exerciseStateId);
	}
	
	@Test
	public void testDeleteFile() throws Exception {
		uploadExerciseService.uploadFile(file, exerciseStateId);
		uploadExerciseService.deleteFile(exerciseStateId);
		File file = new File(UploadExerciseService.getUploadDirectory() +"/"+ exerciseState.getInputState());
		assertFalse(file.exists());
	}
	
	@Test
	public void testGetUploadedFiles() throws Exception {
		Topic topic = topicRepository.findAll().iterator().next();
		Page page = pageRepository.findByTitle("Page 2");
		GameState gameState = gameStateRepository.findByUserId(user.getId()).get(0);
		UploadExercise uploadExercise = new UploadExercise();
		uploadExercise.setPage(page);
		componentRepository.save(uploadExercise);
		
		PageState pageState = pageStateService.initializeStateForNewPage(page, user);
		exerciseStateService.initializeStateForNewPage(pageState);
		var exerciseStates = exerciseStateService.getUserExerciseStates(gameState.getId());
		for(ExerciseState exerciseState : exerciseStates) {
			if (exerciseState.getExercise() instanceof UploadExercise) {
				exerciseStateId = exerciseState.getId().replaceAll("[^0-9]","");
			}
		}
		uploadExerciseService.uploadFile(file, exerciseStateId);
		List<File>files = uploadExerciseService.getUploadedFilesForTopic(user.getId(), topic.getId());
		assertNotNull(files);
		assertEquals(files.size(), 1);
	}
}












