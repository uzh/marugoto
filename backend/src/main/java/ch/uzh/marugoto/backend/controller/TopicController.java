package ch.uzh.marugoto.backend.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.service.TopicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class TopicController extends BaseController {

	@Autowired
	private TopicService topicService;
	@Autowired
	private StateService stateService;
	
	@ApiOperation(value = "List all active topics", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("/topics/list")
	public List<Topic> activeTopicList() {
		return topicService.getActiveTopics();
	}
	
	@ApiOperation(value = "Select a topic", authorizations = {@Authorization(value = "apiKey")})
	@GetMapping("/topics/select/{id}")
	public Topic getSelectedTopic(@PathVariable String id) throws AuthenticationException {
		Topic topic = topicService.getTopic(id);
		stateService.startTopic(topic, getAuthenticatedUser());
		return topic;
	}
}