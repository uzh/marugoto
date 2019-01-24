package ch.uzh.marugoto.core.service;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Topic;
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
	public List<Topic> listAll() {
		var allTopics = Lists.newArrayList(topicRepository.findAll());
		List<Topic>activeTopics = new ArrayList<>();
		for (Topic topic : allTopics) {
			if(topic.isActive() == true) {
				activeTopics.add(topic);
			}
		}
		return activeTopics;
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

	/**
	 * Get start page for specific Topic
	 *
	 *
	 * @return page with components
	 */
	public Page getTopicStartPage(String topicId) {
		return topicRepository.findById(topicId).orElseThrow().getStartPage();
	}
}
