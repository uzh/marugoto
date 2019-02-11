package ch.uzh.marugoto.backend.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import ch.uzh.marugoto.backend.test.BaseControllerTest;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@AutoConfigureMockMvc
public class TopicControllerTest extends BaseControllerTest {

	@Autowired
	private TopicRepository topicRepositry;
	@Autowired 
	private PageRepository pageRepository;
	
	@Test
	public void testListAll() throws Exception {
		mvc.perform(authenticate(get("/api/topics/list")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].title", is("TestTopic")));
	}
	
	@Test
	public void testGetSelectedTopic() throws Exception {
		Page page = pageRepository.findByTitle("Page 1");
		var topic = new Topic("Topic1", "icon-topic-1", true,page);
		topicRepositry.save(topic);
        var topicId = topic.getId().replaceAll("[^0-9]","");

		mvc.perform(authenticate(get("/api/topics/select/" + topicId)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title", is("Topic1")));

	}
}
