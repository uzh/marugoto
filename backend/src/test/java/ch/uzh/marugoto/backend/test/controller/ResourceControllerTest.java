package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;

@AutoConfigureMockMvc
public class ResourceControllerTest extends BaseControllerTest {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	public void testFindResource() throws Exception {
		Resource resource = resourceRepository.findAll().iterator().next();

		mvc.perform(authenticate(get("/api/resources/" + resource.getId())))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.path", is("/dummy/path")));
	}

	@Test
	public void testUploadResource() throws Exception {
		InputStream inputStream = new URL("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4").openStream();
		MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", inputStream);
		
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mvc.perform(MockMvcRequestBuilders.multipart("/api/resources/resource/upload")
				.file(file))
			.andDo(print())
			.andExpect(status().isOk());
	}
	@Test
	public void testDeleteResource() throws Exception {
		Resource resource = resourceRepository.findAll().iterator().next();
		mvc.perform(authenticate(delete("/api/resources/" + resource.getId())))
			.andDo(print())
			.andExpect(status().isNoContent());
	}
	
}
