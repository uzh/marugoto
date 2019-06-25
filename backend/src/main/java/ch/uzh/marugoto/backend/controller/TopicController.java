package ch.uzh.marugoto.backend.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.exception.ClassroomLinkExpiredException;
import ch.uzh.marugoto.core.service.ClassroomService;
import ch.uzh.marugoto.core.service.StateService;
import ch.uzh.marugoto.core.service.TopicService;
import ch.uzh.marugoto.core.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
public class TopicController extends BaseController {

	@Autowired
	private TopicService topicService;
	@Autowired
	private StateService stateService;
	@Autowired
	private UserService userService;
	@Autowired
	private ClassroomService classroomService;
	@Autowired
	private Messages messages;
	
	@ApiOperation(value = "List all active topics", authorizations = { @Authorization(value = "apiKey") })
	@GetMapping("/topics/list")
	public List<Topic> activeTopicList() {
		return topicService.getActiveTopics();
	}
	
	@ApiOperation(value = "Select a topic", authorizations = {@Authorization(value = "apiKey")})
	@GetMapping("/topics/select/{id}")
	public Topic getSelectedTopic(@PathVariable String id, @ApiParam(value = "Classroom invitationLinkId") @RequestParam(required = false) String invitationLinkId) throws AuthenticationException, ClassroomLinkExpiredException {
		User authenticatedUser = getAuthenticatedUser();
		if (invitationLinkId != null) {
			Classroom classroom = classroomService.getClassroomByInvitationLink(invitationLinkId);
		
			if(classroom.getCreatedBy().getId().compareTo(authenticatedUser.getId()) == 0) {
				throw new ClassroomLinkExpiredException(messages.get("classroom.selfNotAllowed"));
			}
			if(classroomService.classHasExpired(classroom) == true) {
				throw new ClassroomLinkExpiredException(messages.get("classroomLink.expired"));
			}
		}
		
		Topic topic = topicService.getTopic(id);
		stateService.startTopic(topic, authenticatedUser);
		userService.addUserToClassroom(authenticatedUser, invitationLinkId);
		return topic;
	}
}
