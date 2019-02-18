package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@Service
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;
	
	/**
	 * Returning list of topics
	 * 
	 * @return List<Topic>
	 */
	public List<Topic> getActiveTopics() {
		return topicRepository.findByActiveIsTrue();
	}

	/**
	 * Finds topic by id
	 * 
	 * @param topicId
	 * 
	 * @return Topic
	 */
	public Topic getTopic(String topicId) {
		return topicRepository.findById(topicId).orElseThrow();
	}
}
