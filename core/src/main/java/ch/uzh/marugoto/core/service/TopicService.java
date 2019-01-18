package ch.uzh.marugoto.core.service;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@Service
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;
	
	public List<Topic> listAll() {
		return Lists.newArrayList(topicRepository.findAll());
	}

	public Topic getTopic(String topicId) {
		return topicRepository.findById(topicId).orElseThrow();
	}

	/**
	 * Get start page for specific Topic
	 *
	 *
	 * @return page with components
	 */
	public Page getTopicStartPage() {
		return topicRepository.findAll().iterator().next().getStartPage();
	}
}
